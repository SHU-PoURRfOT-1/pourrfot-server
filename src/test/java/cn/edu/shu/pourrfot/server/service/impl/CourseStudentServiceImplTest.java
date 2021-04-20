package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.model.CourseStudent;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.repository.CourseGroupMapper;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.CourseStudentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Slf4j
class CourseStudentServiceImplTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();

  private final Course course = Course.builder()
    .courseCode("COURSE_STUDENT")
    .courseName("COURSE_STUDENT")
    .classLocation("COURSE_STUDENT")
    .classTime("COURSE_STUDENT")
    .profilePhoto("COURSE_STUDENT")
    .teacherId(999)
    .term("COURSE_STUDENT")
    .createTime(new Date(System.currentTimeMillis()))
    .updateTime(new Date(System.currentTimeMillis()))
    .build();
  private final PourrfotUser student = PourrfotUser.builder()
    .createTime(new Date(System.currentTimeMillis()))
    .updateTime(new Date(System.currentTimeMillis()))
    .username("COURSE_STUDENT")
    .nickname("COURSE_STUDENT")
    .profilePhoto("COURSE_STUDENT")
    .birth(new Date())
    .sex(SexEnum.male)
    .role(RoleEnum.student)
    .email("COURSE_STUDENT")
    .telephone("COURSE_STUDENT")
    .password("COURSE_STUDENT")
    .build();
  private final CourseGroup courseGroup = CourseGroup.builder()
    .createTime(new Date(System.currentTimeMillis()))
    .updateTime(new Date(System.currentTimeMillis()))
    .groupName("COURSE_STUDENT")
    .profilePhoto("COURSE_STUDENT")
    .build();
  private final CourseStudent courseStudent = CourseStudent.builder()
    .studentId(9999)
    .studentName("MOCK")
    .courseId(9999)
    .groupId(9999)
    .build();
  @Autowired
  private CourseStudentService courseStudentService;
  @Autowired
  private CourseMapper courseMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private CourseGroupMapper courseGroupMapper;

  @Transactional
  @Test
  void save() {
    assertThrows(NotFoundException.class, () -> courseStudentService.save(courseStudent));

    courseMapper.insert(course);
    courseStudent.setCourseId(course.getId());
    assertThrows(NotFoundException.class, () -> courseStudentService.save(courseStudent));

    pourrfotUserMapper.insert(student);
    courseStudent.setStudentId(student.getId());
    assertThrows(NotFoundException.class, () -> courseStudentService.save(courseStudent));

    courseGroupMapper.insert(courseGroup.setCourseId(course.getId()));
    courseStudent.setGroupId(courseGroup.getId());
    assertTrue(courseStudentService.save(courseStudent));
  }

  @Transactional
  @Test
  void updateById() {
    assertThrows(NotFoundException.class, () -> courseStudentService.updateById(courseStudent));
    courseMapper.insert(course);
    courseStudent.setCourseId(course.getId());
    pourrfotUserMapper.insert(student);
    courseStudent.setStudentId(student.getId());
    courseGroupMapper.insert(courseGroup.setCourseId(course.getId()));
    courseStudent.setGroupId(courseGroup.getId());
    courseStudentService.save(courseStudent);
    assertTrue(courseStudentService.updateById(courseStudent.setTotalScore(100_00L)));
    assertThrows(NotFoundException.class, () -> courseStudentService.updateById(courseStudent.setGroupId(100)));
    assertThrows(IllegalCRUDOperationException.class,
      () -> courseStudentService.updateById(courseStudent.setStudentName("UPDATE_NAME")));
    assertThrows(IllegalCRUDOperationException.class, () ->
      courseStudentService.updateById(courseStudent.setCourseId(100)));
    assertThrows(IllegalCRUDOperationException.class, () ->
      courseStudentService.updateById(courseStudent.setStudentId(100)));
  }
}
