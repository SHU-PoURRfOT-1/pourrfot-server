package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.ResourceTypeEnum;
import cn.edu.shu.pourrfot.server.exception.OssFileServiceException;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.OssFile;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.service.OssService;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;
import org.testcontainers.containers.MySQLContainer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
class OssFileControllerTest {

  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  @Autowired
  private MockMvc mockMvc;
  private MockMultipartFile file;
  @MockBean
  private OssService ossService;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private CourseMapper courseMapper;

  @BeforeEach
  void setUp() throws Throwable {
    file = new MockMultipartFile("file",
      new BufferedInputStream(new FileInputStream(
        ResourceUtils.getFile("classpath:test-files/test.txt"))));
  }

  @Test
  void uploadFailed() throws Exception {
    given(ossService.uploadFileWithFilename(any(), anyString()))
      .willThrow(new OssFileServiceException("mock", new OSSException("mock")));
    // upload file
    mockMvc.perform(MockMvcRequestBuilders.multipart("/files/cache")
      .file(file)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .param("filename", "test.txt")
      .with(request -> {
        request.setMethod(HttpMethod.POST.name());
        return request;
      }))
      .andExpect(status().isServiceUnavailable());
  }

  @Test
  void integrationTest() throws Exception {
    when(ossService.uploadFileWithKey(any(), anyString())).thenReturn("oss://mock/test.txt");
    when(ossService.uploadFileWithFilename(any(), anyString())).thenReturn("oss://mock/test.txt");
    given(ossService.checkOssObjectExisted(anyString())).willReturn(true);
    when(ossService.createSymbolLink(anyString(), anyString(), any())).thenReturn("oss://mock/symbolLink/test.txt");
    given(ossService.deleteOssObject(anyString())).willReturn(true);
    given(ossService.getOssFileResource(any())).willReturn(file.getResource());
    given(courseMapper.selectById(any())).willReturn(Course.builder()
      .courseName("mock")
      .courseName("mock")
      .id(100)
      .build());
    final OssFile ossFile = OssFile.builder()
      .name("test.txt")
      .ownerId(100)
      .resourceId(100)
      .resourceType(ResourceTypeEnum.courses)
      .ossUrl("Not blank")
      .build();
    // upload file
    mockMvc.perform(MockMvcRequestBuilders.multipart("/files/cache")
      .file(file)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .with(request -> {
        request.setMethod(HttpMethod.POST.name());
        return request;
      }))
      .andExpect(status().isCreated())
      .andDo(result -> ossFile.setOriginOssUrl(result.getResponse().getHeader(HttpHeaders.LOCATION)));
    // create oss-file
    final List<String> locations = new ArrayList<>();
    mockMvc.perform(post("/files/create")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(ossFile))
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andExpect(header().exists(HttpHeaders.LOCATION))
      .andDo(result -> locations.add(Objects.requireNonNull(result.getResponse().getHeader(HttpHeaders.LOCATION))
        .replace("/api", "")));
    // detail
    mockMvc.perform(get(locations.get(0)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.createTime").exists())
      .andExpect(jsonPath("$.data.updateTime").exists())
      .andExpect(jsonPath("$.data.id").exists())
      .andDo(result -> log.info("Detail success: {}", result.getResponse().getContentAsString()));
    // GET detail not found
    mockMvc.perform(get("/files/detail/999"))
      .andExpect(status().isNotFound());
    // download
    mockMvc.perform(get(locations.get(0) + "/stream"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
      .andExpect(content().bytes(file.getBytes()))
      .andDo(result -> log.info("Download success: {}", locations.get(0) + "/stream"));
    // download not found
    mockMvc.perform(get("/files/detail/999/stream")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
    // page
    mockMvc.perform(get("/files/page")
      .param("name", "test")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get("/files/page")
      .param("ownerId", String.valueOf(100))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get("/files/page")
      .param("directory", "/courses/100")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get("/files/page")
      .param("resourceType", "courses")
      .param("resourceId", "100")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));

    // DELETE Delete
    for (String location : locations) {
      location = location.replace("detail", "delete");
      mockMvc.perform(post(location))
        .andExpect(status().isNoContent())
        .andDo(result -> log.info("Delete success: {}", result.getResponse().getContentAsString()));
      mockMvc.perform(post(location))
        .andExpect(status().isNotFound())
        .andDo(result -> log.info("Delete failed because not found: {}", result.getResponse().getContentAsString()));
    }
  }

  @TestConfiguration
  public static class TestConfig {
    @Bean
    @Primary
    public CourseMapper courseMapper() {
      return Mockito.mock(CourseMapper.class);
    }
  }
}
