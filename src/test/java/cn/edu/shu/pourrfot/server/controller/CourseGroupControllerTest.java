package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.GroupingMethodEnum;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.filter.JwtAuthorizationFilter;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.model.CourseStudent;
import cn.edu.shu.pourrfot.server.model.vo.DivideGroupRequest;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.CourseStudentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
@Testcontainers(disabledWithoutDocker = true)
class CourseGroupControllerTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private CourseMapper courseMapper;
  private Course course;
  @Autowired
  private CourseStudentMapper courseStudentMapper;

  @BeforeEach
  void prepare() {
    course = new Course()
      .setClassLocation("classLocation1")
      .setClassTime("classTime1")
      .setCourseCode("courseCode1")
      .setCourseName("courseName1")
      .setProfilePhoto("profilePhoto1")
      .setTeacherId(100)
      .setGroupingMethod(GroupingMethodEnum.FREE)
      .setTerm("term1");
    assertEquals(1, courseMapper.insert(course));
    final UsernamePasswordAuthenticationToken mockAdminAuthenticationToken = new UsernamePasswordAuthenticationToken(
      "mock", "mock", List.of(new JwtAuthorizationFilter.SimpleGrantedAuthority(RoleEnum.admin)));
    mockAdminAuthenticationToken.setDetails(Map.<String, Object>of("id", 100L, "username", "mock",
      "role", "admin"));
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
  }

  @Transactional
  @Test
  void divide1() throws Exception {
    final int studentAmount = 40;
    for (int i = 0; i < studentAmount; i++) {
      assertEquals(1, courseStudentMapper.insert(CourseStudent.builder()
        .courseId(course.getId())
        .studentId(i)
        .studentName("mock-" + i)
        .studentNumber(String.valueOf(i))
        .createTime(new Date())
        .updateTime(new Date())
        .build()));
    }
    final int expectedGroupSize = 7;
    mockMvc.perform(post(String.format("/courses/%d/groups/divide", course.getId()))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(new DivideGroupRequest(GroupingMethodEnum.AVERAGE, expectedGroupSize)))
      .accept(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data").isArray())
      .andExpect(jsonPath("$.data", Matchers.hasSize(40 / 7 + 1)))
      .andDo(result -> log.info("Divide result: {}", result.getResponse().getContentAsString()));
  }

  @Transactional
  @Test
  void divide2() throws Exception {
    mockMvc.perform(post(String.format("/courses/%d/groups/divide", course.getId()))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(new DivideGroupRequest(GroupingMethodEnum.NOT_GROUPING, 0)))
      .accept(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data").isEmpty())
      .andDo(result -> log.info("Divide result: {}", result.getResponse().getContentAsString()));
  }

  @Transactional
  @Test
  void integrationTest() throws Exception {
    final CourseGroup[] newCourseGroups = new CourseGroup[]{new CourseGroup()
      .setCourseId(course.getId())
      .setProfilePhoto("ProfilePhoto1"),
      new CourseGroup()
        .setCourseId(course.getId())
        .setGroupName("hasGroupName")
        .setProfilePhoto("ProfilePhoto2")
    };
    final List<String> locations = new ArrayList<>(newCourseGroups.length);
    // POST create
    for (CourseGroup newCourseGroup : newCourseGroups) {
      mockMvc.perform(post(String.format("/courses/%d/groups/create", course.getId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newCourseGroup))
        .accept(MediaType.APPLICATION_JSON)
      )
        .andExpect(status().isOk())
        .andExpect(header().exists("Location"))
        .andDo(result -> locations.add(Objects.requireNonNull(result.getResponse()
          .getHeader("Location"))
          .replace("/api", "")));
    }
    // GET detail
    for (String location : locations) {
      mockMvc.perform(get(location))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.group.groupName").exists())
        .andExpect(jsonPath("$.data.group.createTime").exists())
        .andExpect(jsonPath("$.data.group.updateTime").exists())
        .andDo(result -> log.info("Detail success: {}", result.getResponse().getContentAsString()));
    }
    // GET detail not found
    mockMvc.perform(get("/courses/1/groups/detail/999"))
      .andExpect(status().isNotFound());
    // GET page
    mockMvc.perform(get(String.format("/courses/%d/groups/page", course.getId()))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(newCourseGroups.length)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get(String.format("/courses/%d/groups/page", course.getId()))
      .param("groupName", "courseName1")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records[0].group.groupName").exists())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    // PUT update
    mockMvc.perform(post(locations.get(0).replace("detail", "update"))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(newCourseGroups[0].setGroupName("UPDATE")))
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.createTime").exists())
      .andExpect(jsonPath("$.data.updateTime").exists())
      .andExpect(jsonPath("$.data.groupName").value("UPDATE"))
      .andDo(result -> log.info("Update success: {}", result.getResponse().getContentAsString()));
    // DELETE Delete
    for (String location : locations) {
      location = location.replace("detail", "delete");
      mockMvc.perform(post(location))
        .andExpect(status().isOk())
        .andDo(result -> log.info("Delete success: {}", result.getResponse().getContentAsString()));
      mockMvc.perform(post(location))
        .andExpect(status().isNotFound())
        .andDo(result -> log.info("Delete failed because not found: {}", result.getResponse().getContentAsString()));
    }
  }
}
