package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class CourseServiceImplTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  private final PourrfotUser teacher = PourrfotUser.builder()
    .username("username1")
    .nickname("nickname1")
    .role(RoleEnum.teacher)
    .build();
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
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private CourseService courseService;

  @Transactional
  @Test
  void save() {
    assertThrows(NotFoundException.class, () -> courseService.save(course));

    assertEquals(1, pourrfotUserMapper.insert(teacher));
    assertTrue(courseService.save(course.setTeacherId(teacher.getId())));
  }

  @Transactional
  @Test
  void updateById() {
    assertEquals(1, pourrfotUserMapper.insert(teacher));
    assertTrue(courseService.save(course.setTeacherId(teacher.getId())));

    assertTrue(courseService.updateById(course.setProfilePhoto("UPDATE")));
    assertEquals("UPDATE", course.getProfilePhoto());
    assertThrows(IllegalCRUDOperationException.class, () -> courseService.updateById(course.setTeacherId(999)));
    assertThrows(NotFoundException.class, () -> courseService.updateById(course.setId(999)));
  }
}
