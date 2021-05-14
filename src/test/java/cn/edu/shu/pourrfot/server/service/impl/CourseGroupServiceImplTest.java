package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.GroupingMethodEnum;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.filter.JwtAuthorizationFilter;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.model.CourseStudent;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.CourseStudentMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.CourseGroupService;
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
import org.springframework.util.CollectionUtils;
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
class CourseGroupServiceImplTest {
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
    .build();

  @Autowired
  private CourseGroupService courseGroupService;
  @Autowired
  private CourseMapper courseMapper;
  @Autowired
  private CourseStudentMapper courseStudentMapper;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.getContext().setAuthentication(null);
  }

  @Transactional
  @Test
  void pageWithStudentContext() {
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    Page<CourseGroup> page = courseGroupService.page(new Page<>(1, 10),
      new QueryWrapper<>(new CourseGroup().setCourseId(mockCourse.getId()))
        .eq(CourseGroup.COL_COURSE_ID, mockCourse.getId()));
    assertEquals(0, page.getTotal());
    assertTrue(CollectionUtils.isEmpty(page.getRecords()));

    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(courseMapper.selectByStudentId(eq(student.getId()))).willReturn(List.of(mockCourse));
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));
    page = courseGroupService.page(new Page<>(1, 10),
      new QueryWrapper<>(new CourseGroup().setCourseId(mockCourse.getId()))
        .eq(CourseGroup.COL_COURSE_ID, mockCourse.getId()));
    assertEquals(1, page.getTotal());
    assertEquals(1, page.getRecords().size());
  }

  @Transactional
  @Test
  void saveWithAdminContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));
  }

  @Transactional
  @Test
  void saveFailedBecauseNotFoundCourse() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(null);
    assertThrows(NotFoundException.class, () -> courseGroupService.save(mockCourseGroup),
      "Can't create a group with a non-existed course");
  }

  @Transactional
  @Test
  void saveFailedBecauseCourseNoGrouping() {
    mockCourse.setGroupingMethod(GroupingMethodEnum.NOT_GROUPING);
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.save(mockCourseGroup),
      "This course doesn't support grouping");
    // reset
    mockCourse.setGroupingMethod(GroupingMethodEnum.FREE);
  }

  @Transactional
  @Test
  void saveWithTeacherContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));
  }

  @Transactional
  @Test
  void saveWithTeacherContextFailedBecauseCourseNotBelongToHis() {
    mockCourse.setTeacherId(999);
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.save(mockCourseGroup),
      "Teacher can't create a group with a course which isn't belong to his/her");
    // reset
    mockCourse.setTeacherId(teacher.getId());
  }

  @Transactional
  @Test
  void saveWithStudentContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(courseMapper.selectByStudentId(eq(student.getId()))).willReturn(List.of(mockCourse));
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));
  }

  @Transactional
  @Test
  void saveWithStudentContextAndExistedCourseStudent() {
    assertEquals(1, courseStudentMapper.insert(CourseStudent.builder()
      .courseId(mockCourse.getId())
      .studentId(student.getId())
      .studentName("mock")
      .build()));
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(courseMapper.selectByStudentId(eq(student.getId()))).willReturn(List.of(mockCourse));
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));
  }

  @Transactional
  @Test
  void saveWithStudentContextFailedBecauseCourseIsStrictControlledGrouping() {
    mockCourse.setGroupingMethod(GroupingMethodEnum.STRICT_CONTROLLED);
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.save(mockCourseGroup),
      "This course doesn't support grouping by student");
    // reset
    mockCourse.setGroupingMethod(GroupingMethodEnum.FREE);
  }

  @Transactional
  @Test
  void saveWithStudentContextFailedBecauseStudentDoesNotHaveTheCourse() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(courseMapper.selectByStudentId(eq(student.getId()))).willReturn(List.of(Course.builder()
      .id(401)
      .courseCode("course-mock")
      .courseName("course-mock")
      .classLocation("course-mock")
      .classTime("course-mock")
      .profilePhoto("course-mock")
      .teacherId(teacher.getId())
      .term("course-mock")
      .groupingMethod(GroupingMethodEnum.FREE)
      .build()));
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.save(mockCourseGroup),
      "Student can't create a group with a course which isn't belong to his/her");
  }

  @Transactional
  @Test
  void saveWithStudentContextFailedBecauseStudentHadGroupYet() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(courseMapper.selectByStudentId(eq(student.getId()))).willReturn(List.of(mockCourse));
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.save(mockCourseGroup),
      "The course only support one student with one group");
  }

  @Transactional
  @Test
  void updateById() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    assertTrue(courseGroupService.save(mockCourseGroup));
    mockCourseGroup.setGroupName("UPDATE");
    assertTrue(courseGroupService.updateById(mockCourseGroup));
    // reset
    mockCourseGroup.setGroupName("");
  }

  @Transactional
  @Test
  void updateByIdFailedBecauseNotFoundGroup() {
    assertThrows(NotFoundException.class, () -> courseGroupService.updateById(mockCourseGroup),
      "Can't update the group because not found the group");
  }

  @Transactional
  @Test
  void updateByIdFailedBecauseUpdateImmutableFields() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));
    mockCourseGroup.setCourseId(999);
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.updateById(mockCourseGroup),
      "Can't modify the group's course");
    // reset
    mockCourseGroup.setCourseId(mockCourse.getId());
  }

  @Transactional
  @Test
  void updateByIdWithStudentContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(courseMapper.selectByStudentId(eq(student.getId()))).willReturn(List.of(mockCourse));
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));

    mockCourseGroup.setGroupName("UPDATE");
    assertTrue(courseGroupService.updateById(mockCourseGroup));
    // reset
    mockCourseGroup.setGroupName("");
  }

  @Transactional
  @Test
  void updateByIdWithStudentContextFailedBecauseGroupIsNotHis() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(courseMapper.selectByStudentId(eq(student.getId()))).willReturn(List.of(mockCourse));
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));

    mockStudentAuthenticationToken.setDetails(Map.of("id", 999L, "username", "mock",
      "role", "student", "nickname", "student"));
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    mockCourseGroup.setGroupName("UPDATE");
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.updateById(mockCourseGroup),
      "Student can't update a group not belong to his/her");
    // reset
    mockCourseGroup.setGroupName("");
  }

  @Transactional
  @Test
  void updateByIdWithTeacherContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));

    mockCourseGroup.setGroupName("UPDATE");
    assertTrue(courseGroupService.updateById(mockCourseGroup));
    // reset
    mockCourseGroup.setGroupName("");
  }

  @Transactional
  @Test
  void updateByIdWithTeacherContextFailedBecauseGroupIsNotHis() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));


    mockTeacherAuthenticationToken.setDetails(Map.of("id", 999L, "username", "mock",
      "role", "teacher"));
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    mockCourseGroup.setGroupName("UPDATE");
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.updateById(mockCourseGroup),
      "Teacher can't update a group with a course which isn't belong to his/her");
    // reset
    mockCourseGroup.setGroupName("");
  }

  @Transactional
  @Test
  void removeById() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    assertTrue(courseGroupService.save(mockCourseGroup));

    assertTrue(courseGroupService.removeById(mockCourseGroup.getId()));
  }

  @Transactional
  @Test
  void removeByIdFailedBecauseGroupIsNotExisted() {
    assertFalse(courseGroupService.removeById(999));
  }

  @Transactional
  @Test
  void removeByIdWithStudentContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(courseMapper.selectByStudentId(eq(student.getId()))).willReturn(List.of(mockCourse));
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));

    assertTrue(courseGroupService.removeById(mockCourseGroup.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithStudentContextFailedBecauseGroupIsNotHis() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    given(courseMapper.selectByStudentId(eq(student.getId()))).willReturn(List.of(mockCourse));
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));

    mockStudentAuthenticationToken.setDetails(Map.of("id", 900L, "username", "mock",
      "role", "student", "nickname", "student"));
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.removeById(mockCourseGroup.getId()),
      "Student can't delete a group not belong to his/her");
  }

  @Transactional
  @Test
  void removeByIdWithStudentContextFailedBecauseCourseIsStrictControlledGrouping() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    assertTrue(courseGroupService.save(mockCourseGroup));

    mockCourse.setGroupingMethod(GroupingMethodEnum.STRICT_CONTROLLED);
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.removeById(mockCourseGroup.getId()),
      "Student can't delete a group in a strict-controlled-grouping course");

    // reset
    mockCourse.setGroupingMethod(GroupingMethodEnum.FREE);
  }

  @Transactional
  @Test
  void removeByIdWithTeacherContext() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));

    assertTrue(courseGroupService.removeById(mockCourseGroup.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithTeacherContextFailedBecauseGroupIsNotInHisCourse() {
    given(courseMapper.selectById(eq(mockCourse.getId()))).willReturn(mockCourse);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(courseGroupService.save(mockCourseGroup));

    mockTeacherAuthenticationToken.setDetails(Map.of("id", 999L, "username", "mock",
      "role", "teacher"));
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.removeById(mockCourseGroup.getId()),
      "Teacher can't delete a group not belong to his/her course");
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
  }
}
