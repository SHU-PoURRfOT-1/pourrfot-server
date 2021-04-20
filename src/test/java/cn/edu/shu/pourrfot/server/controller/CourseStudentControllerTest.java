package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.model.*;
import cn.edu.shu.pourrfot.server.repository.CourseGroupMapper;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
class CourseStudentControllerTest {

  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();

  private final PourrfotUser[] students = new PourrfotUser[]{
    PourrfotUser.builder()
      .createTime(new Date(System.currentTimeMillis()))
      .updateTime(new Date(System.currentTimeMillis()))
      .username("COURSE_STUDENT1")
      .nickname("COURSE_STUDENT1")
      .profilePhoto("COURSE_STUDENT1")
      .birth(new Date())
      .sex(SexEnum.male)
      .role(RoleEnum.student)
      .email("COURSE_STUDENT1")
      .telephone("COURSE_STUDENT1")
      .password("COURSE_STUDENT1")
      .build(),
    PourrfotUser.builder()
      .createTime(new Date(System.currentTimeMillis()))
      .updateTime(new Date(System.currentTimeMillis()))
      .username("COURSE_STUDENT2")
      .nickname("COURSE_STUDENT2")
      .profilePhoto("COURSE_STUDENT2")
      .birth(new Date())
      .sex(SexEnum.male)
      .role(RoleEnum.student)
      .email("COURSE_STUDENT2")
      .telephone("COURSE_STUDENT2")
      .password("COURSE_STUDENT2")
      .build()
  };
  private final Course course = Course.builder()
    .courseCode("COURSE_STUDENT1")
    .courseName("COURSE_STUDENT1")
    .classLocation("COURSE_STUDENT1")
    .classTime("COURSE_STUDENT1")
    .profilePhoto("COURSE_STUDENT1")
    .teacherId(999)
    .term("COURSE_STUDENT1")
    .createTime(new Date(System.currentTimeMillis()))
    .updateTime(new Date(System.currentTimeMillis()))
    .build();
  private final CourseGroup[] courseGroups = new CourseGroup[]{
    CourseGroup.builder()
      .createTime(new Date(System.currentTimeMillis()))
      .updateTime(new Date(System.currentTimeMillis()))
      .groupName("COURSE_STUDENT1")
      .profilePhoto("COURSE_STUDENT1")
      .build(),
    CourseGroup.builder()
      .createTime(new Date(System.currentTimeMillis()))
      .updateTime(new Date(System.currentTimeMillis()))
      .groupName("COURSE_STUDENT2")
      .profilePhoto("COURSE_STUDENT2")
      .build()
  };
  private final CourseStudent[] courseStudents = new CourseStudent[]{
    CourseStudent.builder()
      .studentId(9999)
      .studentName("MOCK1")
      .courseId(9999)
      .groupId(9999)
      .build(),
    CourseStudent.builder()
      .studentId(9999)
      .studentName("MOCK2")
      .courseId(9999)
      .groupId(9999)
      .build()
  };
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private CourseMapper courseMapper;
  @Autowired
  private CourseGroupMapper courseGroupMapper;


  @BeforeEach
  void prepare() {
    courseMapper.insert(course);
    Arrays.stream(courseGroups).forEach(courseGroup -> courseGroupMapper.insert(courseGroup.setCourseId(course.getId())));
    Arrays.stream(students).forEach(student -> pourrfotUserMapper.insert(student));
    assertEquals(courseStudents.length, courseGroups.length);
    assertEquals(courseStudents.length, students.length);
    for (int i = 0; i < courseStudents.length; i++) {
      courseStudents[i]
        .setCourseId(course.getId())
        .setGroupId(courseGroups[i].getId())
        .setStudentId(students[i].getId());
      assertNotNull(courseStudents[i].getCourseId());
      assertNotNull(courseStudents[i].getStudentId());
      assertNotNull(courseStudents[i].getGroupId());
    }
  }

  @Transactional
  @Test
  void integrationTest() throws Exception {
    // create first
    final List<String> courseStudentLocations = new ArrayList<>(courseStudents.length);
    for (CourseStudent courseStudent : courseStudents) {
      mockMvc.perform(post(String.format("/courses/%d/students", course.getId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(courseStudent))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andDo(result -> courseStudentLocations.add(Objects.requireNonNull(result.getResponse()
          .getHeader(HttpHeaders.LOCATION))
          .replace("/api", "")))
        .andDo(result -> log.info("Create course-student: {} success", result.getResponse().getHeader(HttpHeaders.LOCATION)));
    }
    // GET detail
    for (String location : courseStudentLocations) {
      mockMvc.perform(get(location))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.studentId").exists())
        .andExpect(jsonPath("$.studentName").exists())
        .andExpect(jsonPath("$.courseId").exists())
        .andExpect(jsonPath("$.groupId").exists())
        .andDo(result -> log.info("Detail success: {}", result.getResponse().getContentAsString()));
    }
    // GET page
    mockMvc.perform(get(String.format("/courses/%d/students", course.getId()))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.records").exists())
      .andExpect(jsonPath("$.records").isArray())
      .andExpect(jsonPath("$.records", Matchers.hasSize(courseStudents.length)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    mockMvc.perform(get(String.format("/courses/%d/students", course.getId()))
      .param("groupId", courseStudents[0].getGroupId().toString())
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.records").exists())
      .andExpect(jsonPath("$.records").isArray())
      .andExpect(jsonPath("$.records", Matchers.hasSize(1)))
      .andDo(result -> log.info("Page success: {}", result.getResponse().getContentAsString()));
    // PUT update
    mockMvc.perform(put(courseStudentLocations.get(0))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(courseStudents[0].setTotalScore(100_00L)
        .setScoreStructure(List.of(ScoreItem.builder()
          .score(100.0)
          .weight(1.0)
          .description("Total")
          .build()))))
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.createTime").exists())
      .andExpect(jsonPath("$.updateTime").exists())
      .andExpect(jsonPath("$.totalScore").value(100_00L))
      .andExpect(jsonPath("$.scoreStructure").isArray())
      .andExpect(jsonPath("$.scoreStructure", Matchers.hasSize(1)))
      .andDo(result -> log.info("Update success: {}", result.getResponse().getContentAsString()));
    // DELETE Delete
    for (String location : courseStudentLocations) {
      mockMvc.perform(delete(location))
        .andExpect(status().isNoContent())
        .andDo(result -> log.info("Delete success: {}", result.getResponse().getContentAsString()));
      mockMvc.perform(delete(location))
        .andExpect(status().isNotFound())
        .andDo(result -> log.info("Delete failed because not found: {}", result.getResponse().getContentAsString()));
    }
  }
}
