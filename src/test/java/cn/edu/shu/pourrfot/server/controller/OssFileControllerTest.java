package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.ResourceTypeEnum;
import cn.edu.shu.pourrfot.server.model.OssFile;
import cn.edu.shu.pourrfot.server.service.OssService;
import com.aliyun.oss.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;
import org.testcontainers.containers.MySQLContainer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TODO: The @MockBean seems to conflict with TestContainer, making all Mybatis operations fail due to lack of permissions.
 */
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

  @BeforeEach
  void setUp() throws Throwable {
    file = new MockMultipartFile("file",
      new BufferedInputStream(new FileInputStream(
        ResourceUtils.getFile("classpath:test-files/test.txt"))));
    when(ossService.uploadFileWithKey(any(), anyString())).thenReturn("oss://mock/test.txt");
    when(ossService.uploadFileWithFilename(any(), anyString())).thenReturn("oss://mock/test.txt");
    given(ossService.checkOssObjectExisted(anyString())).willAnswer((Answer<Void>) invocation -> null);
    when(ossService.createSymbolLink(anyString(), anyString(), any())).thenReturn("oss://mock/symbolLink");
  }

  @Test
  void integrationTest() throws Exception {
    final OssFile ossFile = OssFile.builder()
      .name("test.txt")
      .ownerId(100)
      .resourceId(100)
      .resourceType(ResourceTypeEnum.courses)
      .build();
    // upload file
    mockMvc.perform(MockMvcRequestBuilders.multipart("/files/cache")
      .file(file)
      .param("filename", "test.txt")
      .with(request -> {
        request.setMethod(HttpMethod.POST.name());
        return request;
      }))
      .andExpect(status().isCreated())
      .andDo(result -> ossFile.setOriginOssUrl(result.getResponse().getHeader(HttpHeaders.LOCATION)));
  }
}
