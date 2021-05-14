package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.filter.JwtAuthorizationFilter;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseStudent;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.repository.CourseStudentMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class CourseServiceImplTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  private final PourrfotUser teacher = PourrfotUser.builder()
    .username("teacher")
    .nickname("teacher-mock")
    .password("teacher")
    .role(RoleEnum.teacher)
    .id(100)
    .build();
  private final Map<String, Object> teacherDetail = Map.of("id", ((long) teacher.getId()), "username", "mock",
    "role", "teacher");
  private final PourrfotUser admin = PourrfotUser.builder()
    .username("admin")
    .nickname("admin-mock")
    .password("admin")
    .role(RoleEnum.admin)
    .id(300)
    .build();
  private final Map<String, Object> adminDetail = Map.of("id", ((long) admin.getId()), "username", "mock",
    "role", "admin");
  private final Course mockCourse = Course.builder()
    .id(400)
    .courseCode("course-mock")
    .courseName("course-mock")
    .classLocation("course-mock")
    .classTime("course-mock")
    .profilePhoto("course-mock")
    .teacherId(100)
    .term("course-mock")
    .createTime(new Date(System.currentTimeMillis()))
    .updateTime(new Date(System.currentTimeMillis()))
    .build();
  private final PourrfotUser mockStudent = PourrfotUser.builder()
    .id(200)
    .password("student")
    .nickname("student")
    .username("student")
    .role(RoleEnum.student)
    .build();
  private final Map<String, Object> studentDetail = Map.of("id", ((long) mockStudent.getId()), "username", "mock",
    "role", "student");
  private final UsernamePasswordAuthenticationToken mockAdminAuthenticationToken = new UsernamePasswordAuthenticationToken(
    "mock", "mock", List.of(new JwtAuthorizationFilter.SimpleGrantedAuthority(RoleEnum.admin)));
  private final UsernamePasswordAuthenticationToken mockTeacherAuthenticationToken = new UsernamePasswordAuthenticationToken(
    "mock", "mock", List.of(new JwtAuthorizationFilter.SimpleGrantedAuthority(RoleEnum.teacher)));
  private final UsernamePasswordAuthenticationToken mockStudentAuthenticationToken = new UsernamePasswordAuthenticationToken(
    "mock", "mock", List.of(new JwtAuthorizationFilter.SimpleGrantedAuthority(RoleEnum.student)));
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private CourseService courseService;
  @Autowired
  private CourseStudentMapper courseStudentMapper;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.getContext().setAuthentication(null);
  }

  @Transactional
  @Test
  void saveWithNoUserContext() {
    // no user context
    SecurityContextHolder.getContext().setAuthentication(null);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(courseService.save(mockCourse));
  }

  @Transactional
  @Test
  void saveWithAdminUserContext() {
    // admin user context
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(courseService.save(mockCourse));
  }

  @Transactional
  @Test
  void saveWithTeacherUserContext() {
    // teacher user context
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(courseService.save(mockCourse));
  }

  @Transactional
  @Test
  void saveWithTeacherUserContextFailed() {
    // teacher user context but create not-own course
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    given(pourrfotUserMapper.selectById(eq(999))).willReturn(PourrfotUser.builder()
      .username("teacher999")
      .nickname("teacher-mock")
      .password("teacher")
      .role(RoleEnum.teacher)
      .id(999)
      .build());
    assertThrows(IllegalCRUDOperationException.class, () -> courseService.save(Course.builder()
      .courseCode("course-mock")
      .courseName("course-mock")
      .classLocation("course-mock")
      .classTime("course-mock")
      .profilePhoto("course-mock")
      .teacherId(999)
      .term("course-mock")
      .createTime(new Date(System.currentTimeMillis()))
      .updateTime(new Date(System.currentTimeMillis()))
      .build()));
  }

  @Transactional
  @Test
  void saveWithAdminContextButFailed() {
    // admin user context but create non-existed teacher's course
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertThrows(NotFoundException.class, () -> courseService.save(Course.builder()
      .courseCode("course-mock")
      .courseName("course-mock")
      .classLocation("course-mock")
      .classTime("course-mock")
      .profilePhoto("course-mock")
      .teacherId(999)
      .term("course-mock")
      .createTime(new Date(System.currentTimeMillis()))
      .updateTime(new Date(System.currentTimeMillis()))
      .build()));
  }


  @Transactional
  @Test
  void pageIntegrationTest() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    given(pourrfotUserMapper.selectById(eq(101))).willReturn(PourrfotUser.builder()
      .username("teacher101")
      .nickname("teacher-mock-101")
      .password("teacher101")
      .role(RoleEnum.teacher)
      .id(101)
      .build());
    final int total = 50;
    final List<Course> studentCourses = new ArrayList<>();
    for (int i = 0; i < total; i++) {
      final Course toCreateCourse = Course.builder()
        .courseCode("course-mock-" + i)
        .courseName("course-mock-" + i)
        .classLocation("course-mock-" + i)
        .classTime("course-mock-" + i)
        .profilePhoto("course-mock-" + i)
        .teacherId(i % 2 == 0 ? 100 : 101)
        .term("course-mock-" + i)
        .build();
      assertTrue(courseService.save(toCreateCourse));
      if (i % 5 == 0) {
        studentCourses.add(toCreateCourse);
        courseStudentMapper.insert(CourseStudent.builder()
          .studentId(mockStudent.getId())
          .studentName(mockStudent.getNickname())
          .courseId(toCreateCourse.getId())
          .build());
      }
    }
    assertEquals(total / 5, studentCourses.size());
    // no user context
    SecurityContextHolder.getContext().setAuthentication(null);
    Page<Course> result = courseService.page(new Page<>(1, 100), new QueryWrapper<>(new Course()));
    assertEquals(total, result.getTotal());
    assertEquals(100, result.getSize());
    assertEquals(1, result.getCurrent());
    assertEquals(total, result.getRecords().size());
    // admin user context
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    result = courseService.page(new Page<>(1, 100), new QueryWrapper<>(new Course()));
    assertEquals(100, result.getSize());
    assertEquals(1, result.getCurrent());
    assertEquals(total, result.getRecords().size());
    // teacher context
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    result = courseService.page(new Page<>(1, 100), new QueryWrapper<>(new Course()));
    assertEquals(100, result.getSize());
    assertEquals(1, result.getCurrent());
    assertEquals(total / 2, result.getRecords().size());
    // student context
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    result = courseService.page(new Page<>(1, 100), new QueryWrapper<>(new Course()));
    assertEquals(100, result.getSize());
    assertEquals(1, result.getCurrent());
    assertEquals(total / 5, result.getRecords().size());
  }

  @Transactional
  @Test
  void updateByIdWithNoUserContext() {
    SecurityContextHolder.getContext().setAuthentication(null);
    saveWithNoUserContext();
    assertTrue(courseService.updateById(mockCourse.setTerm("UPDATE")));
    // reset
    mockCourse.setTerm("course-mock");
  }

  @Transactional
  @Test
  void updateByIdWithAdminUserContext() {
    saveWithNoUserContext();
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    assertTrue(courseService.updateById(mockCourse.setTerm("UPDATE")));
    // reset
    mockCourse.setTerm("course-mock");
  }

  @Transactional
  @Test
  void updateByIdWithAdminUserContextButFailedBecauseIllegalOperation() {
    saveWithNoUserContext();
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    mockCourse.setTeacherId(999);
    assertThrows(IllegalCRUDOperationException.class, () -> courseService.updateById(mockCourse));
    // reset
    mockCourse.setTeacherId(100);
  }

  @Transactional
  @Test
  void updateByIdWithAdminUserContextButFailed() {
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    assertThrows(NotFoundException.class, () -> courseService.updateById(mockCourse.setTerm("UPDATE")));
    // reset
    mockCourse.setTerm("course-mock");
  }

  @Transactional
  @Test
  void updateByIdWithTeacherUserContext() {
    saveWithNoUserContext();
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(courseService.updateById(mockCourse.setTerm("UPDATE")));
    // reset
    mockCourse.setTerm("course-mock");
  }

  @Transactional
  @Test
  void updateByIdWithTeacherUserContextFailedBecauseNonExistedCourse() {
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    mockCourse.setTerm("UPDATE");
    assertThrows(NotFoundException.class, () -> courseService.updateById(mockCourse));
    // reset
    mockCourse.setTerm("course-mock");
  }

  @Transactional
  @Test
  void updateByIdWithTeacherUserContextFailedBecauseNotOwnCourse() {
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseService.updateById(mockCourse.setTeacherId(999)));
    // reset
    mockCourse.setTeacherId(100);
  }

  @Transactional
  @Test
  void removeByIdWithNoContext() {
    saveWithNoUserContext();
    SecurityContextHolder.getContext().setAuthentication(null);
    assertTrue(courseService.removeById(mockCourse.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithAdminContext() {
    saveWithNoUserContext();
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    assertTrue(courseService.removeById(mockCourse.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithTeacherContext() {
    saveWithNoUserContext();
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(courseService.removeById(mockCourse.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithTeacherContextFailedBecauseNotFound() {
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertFalse(courseService.removeById(mockCourse.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithTeacherContextFailedBecauseNotOwnCourse() {
    saveWithNoUserContext();
    mockTeacherAuthenticationToken.setDetails(Map.of("id", 999L, "username", "mock",
      "role", "teacher"));
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseService.removeById(mockCourse.getId()));
  }

  @TestConfiguration
  public static class TestConfig {
    @Bean
    @Primary
    public PourrfotUserMapper pourrfotUserMapper() {
      return Mockito.mock(PourrfotUserMapper.class);
    }
  }
}
