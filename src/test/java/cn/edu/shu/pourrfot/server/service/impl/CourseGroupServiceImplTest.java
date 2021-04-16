package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.service.CourseGroupService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CourseGroupServiceImplTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  @Autowired
  private CourseGroupService courseGroupService;
  @Autowired
  private CourseMapper courseMapper;
  private Course course;

  @BeforeEach
  void prepare() {
    course = new Course()
      .setCourseCode("courseCode1")
      .setCourseName("TEST")
      .setProfilePhoto("profilePhoto1")
      .setTeacherId(100)
      .setTerm("term1")
      .setId(1);
    assertEquals(1, courseMapper.insert(course));
  }

  @Transactional
  @Test
  void saveWithEmptyGroupName() {
    final CourseGroup toCreate = CourseGroup.builder()
      .courseId(course.getId())
      .profilePhoto("mock")
      .createTime(new Date(System.currentTimeMillis()))
      .updateTime(new Date(System.currentTimeMillis()))
      .build();
    assertTrue(courseGroupService.save(toCreate));
    assertEquals(String.format("TEST-第%d小组", toCreate.getId()), toCreate.getGroupName());
  }

  @Transactional
  @Test
  void saveWithNonExistedCourse() {
    final CourseGroup toCreate = CourseGroup.builder()
      .courseId(999)
      .profilePhoto("mock")
      .createTime(new Date(System.currentTimeMillis()))
      .updateTime(new Date(System.currentTimeMillis()))
      .build();
    assertThrows(NotFoundException.class, () -> courseGroupService.save(toCreate));
  }

  @Transactional
  @Test
  void updateWithIllegalUpdateOperation() {
    final CourseGroup toCreate = CourseGroup.builder()
      .courseId(course.getId())
      .profilePhoto("mock")
      .createTime(new Date(System.currentTimeMillis()))
      .updateTime(new Date(System.currentTimeMillis()))
      .build();
    assertTrue(courseGroupService.save(toCreate));
    assertThrows(IllegalCRUDOperationException.class, () -> courseGroupService.updateById(toCreate.setCourseId(2)));
  }

  @Transactional
  @Test
  void updateWithNotFoundCourseGroup() {
    assertThrows(NotFoundException.class, () -> courseGroupService.updateById(new CourseGroup().setId(999)));
  }
}
