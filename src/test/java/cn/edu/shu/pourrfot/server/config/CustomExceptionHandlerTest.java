package cn.edu.shu.pourrfot.server.config;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.filter.JwtAuthorizationFilter;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
class CustomExceptionHandlerTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private CourseMapper courseMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    final UsernamePasswordAuthenticationToken mockAdminAuthenticationToken = new UsernamePasswordAuthenticationToken(
      "mock", "mock", List.of(new JwtAuthorizationFilter.SimpleGrantedAuthority(RoleEnum.admin)));
    mockAdminAuthenticationToken.setDetails(Map.<String, Object>of("id", 100L, "username", "mock",
      "role", "admin"));
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
  }

  @Test
  void handleIllegalCRUDOperationException() throws Exception {
    final Course found = Course.builder()
      .courseCode("mock")
      .courseName("mock")
      .id(100)
      .teacherId(100)
      .build();
    final Course toUpdate = Course.builder()
      .courseCode("mock")
      .courseName("mock")
      .id(100)
      .teacherId(200)
      .build();
    given(courseMapper.selectById(anyInt())).willReturn(found);
    mockMvc.perform(post("/courses/update/100")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(toUpdate))
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").exists())
      .andExpect(jsonPath("$.message").exists())
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  void handleResourceNotFoundException1() throws Exception {
    final Course toCreate = Course.builder()
      .courseCode("mock")
      .courseName("mock")
      .id(100)
      .teacherId(200)
      .build();
    given(pourrfotUserMapper.selectById(anyInt())).willReturn(null);
    mockMvc.perform(post("/courses/create")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(toCreate))
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().is4xxClientError())
      .andExpect(jsonPath("$.code").exists())
      .andExpect(jsonPath("$.message").exists())
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  void handleResourceNotFoundException2() throws Exception {
    final Course toCreate = Course.builder()
      .courseCode("mock")
      .courseName("mock")
      .id(100)
      .teacherId(200)
      .build();
    given(pourrfotUserMapper.selectById(anyInt())).willReturn(PourrfotUser.builder()
      .id(200)
      .role(RoleEnum.student)
      .build());
    mockMvc.perform(post("/courses/create")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(toCreate))
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().is4xxClientError())
      .andExpect(jsonPath("$.code").exists())
      .andExpect(jsonPath("$.message").exists())
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  void handleDuplicateKeyException() throws Exception {
    final Course toCreate = Course.builder()
      .courseCode("mock")
      .courseName("mock")
      .id(100)
      .teacherId(200)
      .build();
    given(pourrfotUserMapper.selectById(anyInt())).willReturn(PourrfotUser.builder()
      .id(200)
      .role(RoleEnum.teacher)
      .build());
    given(courseMapper.insert(any(Course.class))).willThrow(DuplicateKeyException.class);
    mockMvc.perform(post("/courses/create")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(toCreate))
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().is4xxClientError())
      .andExpect(jsonPath("$.code").exists())
      .andExpect(jsonPath("$.message").exists())
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @TestConfiguration
  public static class TestConfig {

    @Bean
    @Primary
    public CourseMapper courseMapper() {
      return Mockito.spy(CourseMapper.class);
    }

    @Bean
    @Primary
    public PourrfotUserMapper pourrfotUserMapper() {
      return Mockito.spy(PourrfotUserMapper.class);
    }
  }
}
