package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.GroupingMethodEnum;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.filter.JwtAuthorizationFilter;
import cn.edu.shu.pourrfot.server.model.*;
import cn.edu.shu.pourrfot.server.repository.CourseGroupMapper;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.CourseStudentService;
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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;


@SpringBootTest
@Slf4j
@Testcontainers(disabledWithoutDocker = true)
class CourseStudentServiceImplTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  private final PourrfotUser admin = PourrfotUser.builder()
    .username("admin")
    .nickname("admin-mock")
    .password("admin")
    .role(RoleEnum.admin)
    .id(300)
    .build();
  private final Map<String, Object> adminDetail = Map.of("id", ((long) admin.getId()), "username", "mock",
    "role", "admin");
  private final PourrfotUser teacher = PourrfotUser.builder()
    .username("teacher")
    .nickname("teacher-mock")
    .password("teacher")
    .role(RoleEnum.teacher)
    .id(100)
    .build();
  private final Map<String, Object> teacherDetail = Map.of("id", ((long) teacher.getId()), "username", "mock",
    "role", "teacher");
  private final PourrfotUser student = PourrfotUser.builder()
    .id(200)
    .password("student")
    .nickname("student")
    .username("student")
    .role(RoleEnum.student)
    .build();
  private final Map<String, Object> studentDetail = Map.of("id", ((long) student.getId()), "username", "mock",
    "role", "student", "nickname", "student");
  private final UsernamePasswordAuthenticationToken mockAdminAuthenticationToken = new UsernamePasswordAuthenticationToken(
    "mock", "mock", List.of(new JwtAuthorizationFilter.SimpleGrantedAuthority(RoleEnum.admin)));
  private final UsernamePasswordAuthenticationToken mockTeacherAuthenticationToken = new UsernamePasswordAuthenticationToken(
    "mock", "mock", List.of(new JwtAuthorizationFilter.SimpleGrantedAuthority(RoleEnum.teacher)));
  private final UsernamePasswordAuthenticationToken mockStudentAuthenticationToken = new UsernamePasswordAuthenticationToken(
    "mock", "mock", List.of(new JwtAuthorizationFilter.SimpleGrantedAuthority(RoleEnum.student)));
  private final Course mockCourse = Course.builder()
    .id(400)
    .courseCode("course-mock")
    .courseName("course-mock")
    .classLocation("course-mock")
    .classTime("course-mock")
    .profilePhoto("course-mock")
    .teacherId(teacher.getId())
    .term("course-mock")
    .groupingMethod(GroupingMethodEnum.FREE)
    .build();
  private final CourseGroup mockCourseGroup = CourseGroup.builder()
    .courseId(mockCourse.getId())
    .groupName("mock")
    .id(500)
    .build();
  private final CourseStudent mockCourseStudent = CourseStudent.builder()
    .studentName(student.getNickname())
    .studentId(student.getId())
    .courseId(mockCourse.getId())
    .groupId(mockCourseGroup.getId())
    .build();
  @Autowired
  private CourseStudentService courseStudentService;
  @Autowired
  private CourseMapper courseMapper;
  @Autowired
  private CourseGroupMapper courseGroupMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.getContext().setAuthentication(null);
  }


  @Transactional
  @Test
  void pageWithStudentContext() {
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    Page<CourseStudent> result = courseStudentService.page(new Page<>(1, 10),
      new QueryWrapper<>(new CourseStudent().setCourseId(mockCourse.getId())));
    assertEquals(0, result.getTotal());
    assertTrue(result.getRecords().isEmpty());

    SecurityContextHolder.getContext().setAuthentication(null);
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));

    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    result = courseStudentService.page(new Page<>(1, 10),
      new QueryWrapper<>(new CourseStudent().setCourseId(mockCourse.getId())));
    assertEquals(1, result.getTotal());
    assertEquals(1, result.getRecords().size());
  }

  @Transactional
  @Test
  void pageWithTeacherContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(courseStudentService.save(mockCourseStudent));

    final Page<CourseStudent> result = courseStudentService.page(new Page<>(1, 10),
      new QueryWrapper<>(new CourseStudent().setCourseId(mockCourse.getId())));
    assertEquals(1, result.getTotal());
    assertEquals(1, result.getRecords().size());
  }

  @Transactional
  @Test
  void pageWithTeacherContextFailedBecauseCourseIsNotHis() {
    mockCourse.setTeacherId(999);
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseStudentService.page(new Page<>(1, 10),
      new QueryWrapper<>(new CourseStudent().setCourseId(mockCourse.getId()))),
      "Teacher can't access the students in the course which isn't belong his/her");
    // reset
    mockCourse.setTeacherId(teacher.getId());
  }

  @Transactional
  @Test
  void getById() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));
    assertNotNull(courseStudentService.getById(mockCourseStudent.getId()));
  }

  @Transactional
  @Test
  void getByIdWithStudentFailedBecauseNotHisCourse() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));

    mockStudentAuthenticationToken.setDetails(Map.of("id", 999L, "username", "mock",
      "role", "student", "nickname", "student"));
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);

    assertThrows(IllegalCRUDOperationException.class, () -> courseStudentService.getById(mockCourseStudent.getId()),
      "Student can't access the student in the course which isn't belong his/her");
  }

  @Transactional
  @Test
  void getByIdWithTeacherContextFailedBecauseNotHisCourse() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));

    mockTeacherAuthenticationToken.setDetails(Map.of("id", 999L, "username", "mock",
      "role", "teacher"));
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);

    assertThrows(IllegalCRUDOperationException.class, () -> courseStudentService.getById(mockCourseStudent.getId()),
      "Teacher can't access the student in the course which isn't belong his/her");
  }

  @Transactional
  @Test
  void saveFailedBecauseCourseNotExisted() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(null);
    assertThrows(NotFoundException.class, () -> courseStudentService.save(mockCourseStudent),
      "Can't create a course-student with a non-exist course");
  }

  @Transactional
  @Test
  void saveFailedBecauseStudentNotExisted() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(null);
    assertThrows(NotFoundException.class, () -> courseStudentService.save(mockCourseStudent),
      "Can't create a course-student with a non-exist student");
  }

  @Transactional
  @Test
  void saveFailedBecauseGroupNotExisted() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(null);
    assertThrows(NotFoundException.class, () -> courseStudentService.save(mockCourseStudent),
      "Can't create a course-student with a non-exist group");
  }

  @Transactional
  @Test
  void saveFailedBecauseGroupCourseDifferent() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    mockCourseGroup.setCourseId(999);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertThrows(IllegalCRUDOperationException.class, () -> courseStudentService.save(mockCourseStudent),
      "Can't create a course-student with a group whose course is different from the student's course");
    // reset
    mockCourseGroup.setCourseId(mockCourse.getId());
  }

  @Transactional
  @Test
  void saveWithStudentContextFailedBecauseCourseIsLimitedGrouping() {
    mockCourse.setGroupingMethod(GroupingMethodEnum.NOT_GROUPING);
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseStudentService.save(mockCourseStudent),
      "Student can't update his/her group because the course's grouping method is limited");
    // reset
    mockCourse.setGroupingMethod(GroupingMethodEnum.FREE);
  }

  @Transactional
  @Test
  void saveWithTeacherContextFailedBecauseCourseIsNotHis() {
    mockCourse.setTeacherId(999);
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseStudentService.save(mockCourseStudent),
      "Teacher can't access the course-student with a not-own course");
    // reset
    mockCourse.setTeacherId(teacher.getId());
  }

  @Transactional
  @Test
  void saveWithAdminContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    assertTrue(courseStudentService.save(mockCourseStudent));
  }

  @Transactional
  @Test
  void saveWithAdminContextFailedBecauseDuplicated() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    assertTrue(courseStudentService.save(mockCourseStudent));
    assertThrows(IllegalCRUDOperationException.class, () -> courseStudentService.save(mockCourseStudent),
      "course-student should be unique but duplicated");
  }

  @Transactional
  @Test
  void saveWithStudentContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertTrue(courseStudentService.save(mockCourseStudent));
  }

  @Transactional
  @Test
  void saveWithTeacherContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(courseStudentService.save(mockCourseStudent));
  }

  @Transactional
  @Test
  void save() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));
  }

  @Transactional
  @Test
  void updateById() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));

    assertTrue(courseStudentService.updateById(mockCourseStudent.setTotalScore(100_00L)));
    mockCourseStudent.setTotalScore(null);
  }

  @Transactional
  @Test
  void updateByIdFailedBecauseCourseStudentNotFound() {
    assertThrows(NotFoundException.class, () -> courseStudentService.updateById(mockCourseStudent),
      "Can't update a non-existed course-student");
  }

  @Transactional
  @Test
  void updateByIdFailedBecauseUpdatedImmutableFields() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));

    assertThrows(IllegalCRUDOperationException.class, () -> courseStudentService.updateById(mockCourseStudent
      .setStudentName("UPDATE")), "Can't update a course-student's immutable fields");
    // reset
    mockCourseStudent.setStudentName("mock");
  }


  @Transactional
  @Test
  void updateByIdWithStudentContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));

    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertTrue(courseStudentService.updateById(mockCourseStudent.setTotalScore(100_00L)
      .setScoreStructure(List.of(ScoreItem.builder()
        .description("mock")
        .weight(1.0)
        .score(100.0)
        .build()))));
    assertEquals(0, mockCourseStudent.getTotalScore());
    assertTrue(mockCourseStudent.getScoreStructure().isEmpty());
    // reset
    mockCourseStudent.setTotalScore(null).setScoreStructure(null);
  }

  @Transactional
  @Test
  void updateByIdWithTeacherContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));

    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(courseStudentService.updateById(mockCourseStudent.setTotalScore(100_00L)
      .setScoreStructure(List.of(ScoreItem.builder()
        .description("mock")
        .weight(1.0)
        .score(100.0)
        .build()))));
    assertEquals(100_00L, mockCourseStudent.getTotalScore());
    assertFalse(mockCourseStudent.getScoreStructure().isEmpty());
    // reset
    mockCourseStudent.setTotalScore(null).setScoreStructure(null);
  }

  @Transactional
  @Test
  void removeById() {
    assertFalse(courseStudentService.removeById(mockCourseStudent.getId()));

    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));

    assertTrue(courseStudentService.removeById(mockCourseStudent.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithTeacherContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));

    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(courseStudentService.removeById(mockCourseStudent.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithTeacherContextFailedBecauseNotHisStudent() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(pourrfotUserMapper.selectById(eq(student.getId()))).willReturn(student);
    given(courseGroupMapper.selectById(eq(mockCourseGroup.getId()))).willReturn(mockCourseGroup);
    assertTrue(courseStudentService.save(mockCourseStudent));

    mockTeacherAuthenticationToken.setDetails(Map.of("id", 999L, "username", "mock",
      "role", "teacher"));
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseStudentService.removeById(mockCourseStudent.getId()),
      "Teacher can't delete the course-student with a not-own course");
  }

  @TestConfiguration
  public static class TestConfig {
    @Bean
    @Primary
    public PourrfotUserMapper pourrfotUserMapper() {
      return Mockito.mock(PourrfotUserMapper.class);
    }

    @Bean
    @Primary
    public CourseMapper courseMapper() {
      return Mockito.mock(CourseMapper.class);
    }

    @Bean
    @Primary
    public CourseGroupMapper groupMapper() {
      return Mockito.mock(CourseGroupMapper.class);
    }
  }
}
