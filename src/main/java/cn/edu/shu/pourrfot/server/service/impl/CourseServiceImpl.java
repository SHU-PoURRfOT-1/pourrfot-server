package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.helper.PageHelper;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.model.CourseStudent;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.dto.SimpleUser;
import cn.edu.shu.pourrfot.server.model.dto.StudentCourse;
import cn.edu.shu.pourrfot.server.repository.CourseGroupMapper;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.CourseStudentMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author spencercjh
 */
@Slf4j
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private CourseGroupMapper courseGroupMapper;
  @Autowired
  private CourseStudentMapper courseStudentMapper;

  @Override
  public <E extends IPage<Course>> E page(E page, Wrapper<Course> wrapper) {
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    // Teacher users can only view their own courses
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      QueryWrapper<Course> queryWrapper = (QueryWrapper<Course>) wrapper;
      queryWrapper.eq(Course.COL_TEACHER_ID, user.getId());
      final E resultPage = super.page(page, queryWrapper);
      resultPage.setRecords(resultPage.getRecords()
        .stream()
        .filter(course -> course.getTeacherId().equals(user.getId()))
        .collect(Collectors.toList()));
      return resultPage;
    }
    // Student users can only view their own courses
    else if (user != null && user.getRole().equals(RoleEnum.student)) {
      final Set<Integer> studentCourseIdSet = baseMapper.selectByStudentId(user.getId())
        .stream()
        .map(Course::getId)
        .collect(Collectors.toSet());
      final List<Course> studentCourses = baseMapper.selectList(wrapper).stream()
        .filter(course -> studentCourseIdSet.contains(course.getId()))
        .collect(Collectors.toList());
      return PageHelper.manuallyPage(studentCourses, page);
    } else {
      return super.page(page, wrapper);
    }
  }

  @Override
  public Page<StudentCourse> studentCoursePage(Page<Course> page, Wrapper<Course> wrapper) {
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user == null || !user.getRole().equals(RoleEnum.student)) {
      throw new IllegalCRUDOperationException("Can't access a specific student course info with wrong user info");
    }
    final Page<Course> studentCoursePage = page(page, wrapper);
    return new Page<StudentCourse>(studentCoursePage.getCurrent(), studentCoursePage.getSize(), studentCoursePage.getTotal())
      .setRecords(studentCoursePage.getRecords()
        .stream()
        .map(course -> {
          final CourseStudent courseStudent = courseStudentMapper.selectOne(new QueryWrapper<>(new CourseStudent())
            .eq(CourseStudent.COL_STUDENT_ID, user.getId()).eq(CourseStudent.COL_COURSE_ID, course.getId()));
          final CourseGroup courseGroup = courseStudent != null ? courseGroupMapper.selectById(courseStudent.getGroupId()) : null;
          final PourrfotUser teacher = pourrfotUserMapper.selectById(course.getTeacherId());
          return StudentCourse.of(course, courseStudent, courseGroup, teacher != null ? teacher.setPassword("******") : null
          );
        })
        .collect(Collectors.toList()));
  }

  @Override
  public Course getById(Serializable id) {
    final Course found = baseMapper.selectById(id);
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    // Teacher users can only view their own courses
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      if (!found.getTeacherId().equals(user.getId())) {
        log.warn("Teacher: {} can't access the course: {} not belong to his/her", user, found);
        throw new IllegalCRUDOperationException("Teacher can't access the course not belong to his/her");
      }
    }
    // Student users can only view their own courses
    else if (user != null && user.getRole().equals(RoleEnum.student)) {
      final Set<Integer> studentCourseIdSet = baseMapper.selectByStudentId(user.getId())
        .stream()
        .map(Course::getId)
        .collect(Collectors.toSet());
      if (!studentCourseIdSet.contains(((int) id))) {
        log.warn("Student: {} can't access the course: {} not belong to his/her", user, found);
        throw new IllegalCRUDOperationException("Student can't access the course not belong to his/her");
      }
    }
    return found;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(Course course) {
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    // only admin and teacher user can access this method
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      if (!course.getTeacherId().equals(user.getId())) {
        log.warn("Teacher user: {} is creating a not-own course: {}", user, course);
        throw new IllegalCRUDOperationException("Teacher user can't create a not-own course");
      }
    }
    final PourrfotUser teacher = pourrfotUserMapper.selectById(course.getTeacherId());
    if (teacher == null || !RoleEnum.teacher.equals(teacher.getRole())) {
      final NotFoundException e = new NotFoundException("Can't create the course with non-existed teacher");
      log.error("Can't save a course because the teacher doesn't exist: {}", course, e);
      throw e;
    }
    final boolean result = baseMapper.insert(course
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
    log.info("User: {} create a course: {}", user != null ? user : "null", course);
    return result;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(Course entity) {
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    // only admin and teacher user can access this method
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      if (!entity.getTeacherId().equals(user.getId())) {
        log.warn("Teacher user: {} is updating a not-own course: {}", user, entity);
        throw new IllegalCRUDOperationException("Teacher user can't update a not-own course");
      }
    }
    final Course found = baseMapper.selectById(entity.getId());
    if (found == null) {
      final NotFoundException e = new NotFoundException("Can't update the course because not found the course");
      log.error("Can't update a non-existed course: {}", entity, e);
      throw e;
    }
    if (!entity.getTeacherId().equals(found.getTeacherId())) {
      final IllegalCRUDOperationException e = new IllegalCRUDOperationException("Can't modify the course's teacher");
      log.warn("Can't update a course's immutable fields: {}", entity, e);
      throw e;
    }
    final boolean result = baseMapper.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime())) == 1;
    log.info("User: {} update a course: {}", user != null ? user : "null", found);
    return result;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean removeById(Serializable id) {
    final Course found = baseMapper.selectById(id);
    if (found == null) {
      return false;
    }
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    // only admin and teacher user can access this method
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      if (!found.getTeacherId().equals(user.getId())) {
        log.warn("Teacher user: {} is deleting a not-own course: {}", user, found);
        throw new IllegalCRUDOperationException("Teacher user can't delete a not-own course");
      }
    }
    // delete the course's all groups and students
    final boolean result = baseMapper.deleteById(id) == 1;
    log.info("User: {} delete a course: {}", user != null ? user : "null", found);
    final boolean deleteGroupsResult = courseGroupMapper.selectCount(new QueryWrapper<>(new CourseGroup())
      .eq(CourseGroup.COL_COURSE_ID, id)) ==
      courseGroupMapper.delete(new QueryWrapper<>(new CourseGroup())
        .eq(CourseGroup.COL_COURSE_ID, id));
    if (!deleteGroupsResult) {
      log.error("Delete course-groups relationship failed anomalously");
      throw new IllegalStateException("Delete course-groups relationship failed anomalously");
    }
    log.info("User: {} delete course: {} 's all groups after deleting the course", user, found);
    final boolean deleteStudentsResult = courseStudentMapper.selectCount(new QueryWrapper<>(new CourseStudent())
      .eq(CourseStudent.COL_COURSE_ID, id)) ==
      courseStudentMapper.delete(new QueryWrapper<>(new CourseStudent())
        .eq(CourseStudent.COL_COURSE_ID, id));
    if (!deleteStudentsResult) {
      log.error("Delete course-students relationship failed anomalously");
      throw new IllegalStateException("Delete course-students relationship failed anomalously");
    }
    log.info("User: {} delete course: {} 's all students after deleting the course", user, found);
    return result;
  }
}
