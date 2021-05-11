package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
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

  @BeforeEach
  void prepare() {
    course = new Course()
      .setClassLocation("classLocation1")
      .setClassTime("classTime1")
      .setCourseCode("courseCode1")
      .setCourseName("courseName1")
      .setProfilePhoto("profilePhoto1")
      .setTeacherId(100)
      .setTerm("term1");
    assertEquals(1, courseMapper.insert(course));
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
      mockMvc.perform(post(String.format("/courses/%d/groups", course.getId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newCourseGroup))
        .accept(MediaType.APPLICATION_JSON)
      )
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andDo(result -> locations.add(Objects.requireNonNull(result.getResponse()
          .getHeader("Location"))
          .replace("/api", "")));
    }
    // GET detail
    for (String location : locations) {
      mockMvc.perform(get(location))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.groupName").exists())
        .andExpect(jsonPath("$.data.createTime").exists())
        .andExpect(jsonPath("$.data.updateTime").exists())
        .andDo(result -> log.info("Detail success: {}", result.getResponse().getContentAsString()));
    }
    // GET detail not found
    mockMvc.perform(get("/courses/1/groups/999"))
      .andExpect(status().isNotFound());
    // GET page
    mockMvc.perform(get(String.format("/courses/%d/groups", course.getId()))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(newCourseGroups.length)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get(String.format("/courses/%d/groups", course.getId()))
      .param("groupName", "courseName1")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.records").exists())
      .andExpect(jsonPath("$.data.records").isArray())
      .andExpect(jsonPath("$.data.records[0].groupName").exists())
      .andExpect(jsonPath("$.data.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    // PUT update
    mockMvc.perform(put(locations.get(0))
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
      mockMvc.perform(delete(location))
        .andExpect(status().isNoContent())
        .andDo(result -> log.info("Delete success: {}", result.getResponse().getContentAsString()));
      mockMvc.perform(delete(location))
        .andExpect(status().isNotFound())
        .andDo(result -> log.info("Delete failed because not found: {}", result.getResponse().getContentAsString()));
    }
  }
}
