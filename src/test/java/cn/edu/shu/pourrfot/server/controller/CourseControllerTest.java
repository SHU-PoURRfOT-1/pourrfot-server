package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.model.Course;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class CourseControllerTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Transactional
  @Test
  void integrationTest() throws Exception {
    final Course[] newCourses = new Course[]{new Course()
      .setClassLocation("classLocation1")
      .setClassTime("classTime1")
      .setCourseCode("courseCode1")
      .setCourseName("courseName1")
      .setProfilePhoto("profilePhoto1")
      .setTeacherId(100)
      .setTerm("term1"),
      new Course().setClassLocation("classLocation2")
        .setClassTime("classTime2")
        .setCourseCode("courseCode2")
        .setCourseName("courseName2")
        .setProfilePhoto("profilePhoto2")
        .setTeacherId(101)
        .setTerm("term2")};
    final List<String> locations = new ArrayList<>(newCourses.length);
    // POST create
    for (Course newCourse : newCourses) {
      mockMvc.perform(post("/courses")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newCourse))
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
        .andExpect(jsonPath("$.createTime").exists())
        .andExpect(jsonPath("$.updateTime").exists())
        .andDo(result -> log.info("Detail success: {}", result.getResponse().getContentAsString()));
    }
    // GET page
    mockMvc.perform(get("/courses")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.records").exists())
      .andExpect(jsonPath("$.records").isArray())
      .andExpect(jsonPath("$.records", Matchers.hasSize(newCourses.length)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get("/courses")
      .param("teacherId", "100")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.records").exists())
      .andExpect(jsonPath("$.records").isArray())
      .andExpect(jsonPath("$.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    // PUT update
    mockMvc.perform(put(locations.get(0))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(newCourses[0].setTeacherId(999)))
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.createTime").exists())
      .andExpect(jsonPath("$.updateTime").exists())
      .andExpect(jsonPath("$.teacherId").value(999))
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