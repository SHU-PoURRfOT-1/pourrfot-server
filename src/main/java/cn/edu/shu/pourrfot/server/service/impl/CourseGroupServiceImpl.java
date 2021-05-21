package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.GroupingMethodEnum;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.model.CourseStudent;
import cn.edu.shu.pourrfot.server.model.dto.CompleteGroup;
import cn.edu.shu.pourrfot.server.model.dto.SimpleUser;
import cn.edu.shu.pourrfot.server.repository.CourseGroupMapper;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.CourseStudentMapper;
import cn.edu.shu.pourrfot.server.service.CourseGroupService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author spencercjh
 */
@Slf4j
@Service
public class CourseGroupServiceImpl extends ServiceImpl<CourseGroupMapper, CourseGroup> implements CourseGroupService {
  @Autowired
  private CourseMapper courseMapper;
  @Autowired
  private CourseStudentMapper courseStudentMapper;

  @Override
  public Page<CompleteGroup> page(Page<CourseGroup> page, Wrapper<CourseGroup> queryWrapper) {
    // there is an existed condition eq(course_id,id) in the wrapper
    final int courseId = queryWrapper.getEntity().getCourseId();
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    // student user can only view their own groups
    if (user != null && user.getRole().equals(RoleEnum.student)) {
      final CourseGroup studentGroup = baseMapper.selectByStudentIdAndCourseId(user.getId(),
        courseId);
      if (studentGroup == null) {
        return new Page<>(page.getCurrent(), page.getSize(), 0);
      }
      final List<CourseStudent> groupStudents = courseStudentMapper.selectList(new QueryWrapper<>(new CourseStudent())
        .eq(CourseStudent.COL_GROUP_ID, studentGroup.getId()));
      return new Page<CompleteGroup>(page.getCurrent(), page.getSize(), 1)
        .setRecords(Collections.singletonList(new CompleteGroup(studentGroup, groupStudents)));
    }
    // teacher user can only view the groups in their own courses
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      final Course course = courseMapper.selectById(courseId);
      if (!course.getTeacherId().equals(user.getId())) {
        log.warn("Teacher: {} can't access the groups in the course: {} which isn't belong his/her", user, course);
        throw new IllegalCRUDOperationException("Teacher can't access the groups in the course which isn't belong his/her");
      }
    }
    final Page<CourseGroup> groupPage = super.page(page, queryWrapper);
    return new Page<CompleteGroup>(page.getCurrent(), page.getSize(), groupPage.getTotal())
      .setRecords(groupPage.getRecords().stream().map(group ->
        new CompleteGroup(group, courseStudentMapper.selectList(new QueryWrapper<>(new CourseStudent())
          .eq(CourseStudent.COL_GROUP_ID, group.getId())))).collect(Collectors.toList()));
  }

  @Override
  public CompleteGroup getCompleteGroupById(int id) {
    final CourseGroup group = getById(id);
    if (group != null) {
      return new CompleteGroup(group, courseStudentMapper.selectList(new QueryWrapper<>(new CourseStudent())
        .eq(CourseStudent.COL_GROUP_ID, group.getId())));
    }
    return null;
  }

  @Override
  public CourseGroup getById(Serializable id) {
    final CourseGroup found = baseMapper.selectById(id);
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user != null && user.getRole().equals(RoleEnum.student)) {
      final Set<Integer> studentGroups = baseMapper.selectByStudentId(user.getId())
        .stream()
        .map(CourseGroup::getId)
        .collect(Collectors.toSet());
      if (!studentGroups.contains(((int) id))) {
        log.warn("Student: {} can't access the course-group: {} not belong to his/her", user, found);
        throw new IllegalCRUDOperationException("Student can't access the course-group not belong to his/her");
      }
    } else if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      final Course foundCourse = courseMapper.selectById(found.getCourseId());
      if (foundCourse == null || !foundCourse.getTeacherId().equals(user.getId())) {
        log.warn("Teacher: {} can't access the course-group: {} not belong to his/her", user, found);
        throw new IllegalCRUDOperationException("Teacher can't access the course-group not belong to his/her");
      }
    }
    return found;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(CourseGroup courseGroup) {
    final Course course = courseMapper.selectById(courseGroup.getCourseId());
    if (course == null) {
      final NotFoundException e = new NotFoundException("Can't create a group with a non-existed course");
      log.error("Save a course group failed because the course doesn't exist: {}", courseGroup, e);
      throw e;
    }
    // anyone can't create a group in NOT_GROUPING course
    if (course.getGroupingMethod().equals(GroupingMethodEnum.NOT_GROUPING)) {
      log.warn("Course: {} doesn't support grouping", course.getId());
      throw new IllegalCRUDOperationException("This course doesn't support grouping");
    }
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      // Teacher can't create a group with a course which isn't belong to his/her
      if (!course.getTeacherId().equals(user.getId())) {
        log.warn("Teacher: {} can't create a group: {} with a course: {} which isn't belong to his/her", user, courseGroup,
          courseGroup.getCourseId());
        throw new IllegalCRUDOperationException("Teacher can't create a group with a course which isn't belong to his/her");
      }
    }
    boolean isStudent = false;
    if (user != null && user.getRole().equals(RoleEnum.student)) {
      isStudent = true;
      // student can't create a group in STRICT_CONTROLLED course
      if (course.getGroupingMethod().equals(GroupingMethodEnum.STRICT_CONTROLLED)) {
        log.warn("Course: {} doesn't support grouping by student but requested by {}", course.getId(), user);
        throw new IllegalCRUDOperationException("This course doesn't support grouping by student");
      }
      // student can't create a group with a course which is not belong to his/her
      final Set<Integer> studentCourses = courseMapper.selectByStudentId(user.getId())
        .stream()
        .map(Course::getId)
        .collect(Collectors.toSet());
      if (!studentCourses.contains(courseGroup.getCourseId())) {
        log.warn("Student: {} can't create a group: {} with a course: {} which isn't belong to his/her", user, courseGroup,
          courseGroup.getCourseId());
        throw new IllegalCRUDOperationException("Student can't create a group with a course which isn't belong to his/her");
      }
      // student who has a group yet can't create a group again
      final CourseGroup foundStudentGroup = baseMapper.selectByStudentIdAndCourseId(user.getId(),
        course.getId());
      if (foundStudentGroup != null) {
        log.warn("Course: {} only support one person with one group, but requested by {}", course.getId(), user);
        throw new IllegalCRUDOperationException("The course only support one student with one group");
      }
    }
    final boolean insertResult = baseMapper.insert(courseGroup
      .setGroupName(setupGroupName(courseGroup, course))
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
    log.info("User: {} create a course-group: {}", user != null ? user : "null", courseGroup);
    if (isStudent) {
      boolean updateCourseStudentResult;
      // create a course-student with group id or update one after saving course group (for the auto-increased id)
      final CourseStudent foundCourseStudent = courseStudentMapper.selectOne(new QueryWrapper<>(new CourseStudent())
        .eq(CourseStudent.COL_STUDENT_ID, user.getId())
        .eq(CourseStudent.COL_COURSE_ID, course.getId()));
      if (foundCourseStudent == null) {
        final CourseStudent newCourseStudent = CourseStudent.builder()
          .courseId(course.getId())
          .groupId(courseGroup.getId())
          .studentId(user.getId())
          .studentName(user.getNickname())
          .studentNumber(user.getUsername())
          .build();
        updateCourseStudentResult = courseStudentMapper.insert(newCourseStudent) == 1;
        log.info("User: {} create a course-student: {}", user, newCourseStudent);
      } else {
        updateCourseStudentResult = courseStudentMapper.updateById(foundCourseStudent.setGroupId(courseGroup.getId())) == 1;
        log.info("User: {} update a course-student: {}", user, foundCourseStudent);
      }
      if (!updateCourseStudentResult) {
        log.error("Create or update course-student relationship failed anomalously");
        throw new IllegalStateException("Create or update course-student relationship failed anomalously, please retry");
      }
    }
    return insertResult;
  }

  private String setupGroupName(CourseGroup entity, Course course) {
    return StringUtils.isNotBlank(entity.getGroupName()) ? entity.getGroupName().trim() :
      course.getCourseName() + "-" + UUID.randomUUID().toString().substring(5);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(CourseGroup entity) {
    final CourseGroup found = baseMapper.selectById(entity.getId());
    if (found == null) {
      final NotFoundException e = new NotFoundException("Can't update the group because not found the group");
      log.error("Can't update a non-existed course group: {}", entity, e);
      throw e;
    }
    if (!entity.getCourseId().equals(found.getCourseId())) {
      final IllegalCRUDOperationException e = new IllegalCRUDOperationException("Can't modify the group's course");
      log.warn("Can't update a course group's immutable fields: {}", entity, e);
      throw e;
    }
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user != null && user.getRole().equals(RoleEnum.student)) {
      // student can't update a group which isn't belong to his/her
      final CourseGroup studentGroup = baseMapper.selectByStudentIdAndCourseId(user.getId(),
        entity.getCourseId());
      if (studentGroup == null || !studentGroup.getId().equals(entity.getId())) {
        log.warn("Student: {} can't update a group: {} not belong to his/her", user, entity.getId());
        throw new IllegalCRUDOperationException("Student can't update a group not belong to his/her");
      }
    }
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      // teacher can't update a group which isn't belong to his/her
      final Course course = courseMapper.selectById(entity.getCourseId());
      if (!course.getTeacherId().equals(user.getId())) {
        log.warn("Teacher: {} can't update a group: {} with a course: {} which isn't belong to his/her", user, entity,
          entity.getCourseId());
        throw new IllegalCRUDOperationException("Teacher can't update a group with a course which isn't belong to his/her");
      }
    }
    final boolean result = baseMapper.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime())) == 1;
    log.info("User: {} update a course-group: {}", user != null ? user : "null", entity);
    return result;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean removeById(Serializable id) {
    final CourseGroup courseGroup = baseMapper.selectById(id);
    if (courseGroup == null) {
      return false;
    }
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    boolean isStudent = false;
    Course course = null;
    if (user != null && user.getRole().equals(RoleEnum.student)) {
      isStudent = true;
      final CourseGroup studentGroup = baseMapper.selectByStudentIdAndCourseId(user.getId(),
        courseGroup.getCourseId());
      // student can't delete a group not belong to his/her
      if (studentGroup == null || !studentGroup.getId().equals(id)) {
        log.warn("Student: {} can't delete a group: {} not belong to his/her", user, id);
        throw new IllegalCRUDOperationException("Student can't delete a group not belong to his/her");
      }
      course = courseMapper.selectById(courseGroup.getCourseId());
      // student can't delete a group in a strict-controlled-grouping course
      if (course == null || course.getGroupingMethod().equals(GroupingMethodEnum.STRICT_CONTROLLED)) {
        log.warn("Student: {} can't delete a group: {} in a strict-controlled-grouping course", user, id);
        throw new IllegalCRUDOperationException("Student can't delete a group in a strict-controlled-grouping course");
      }
    }
    // teacher can't delete a group not in his/her course
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      course = courseMapper.selectById(courseGroup.getCourseId());
      if (course == null || !course.getTeacherId().equals(user.getId())) {
        log.warn("Teacher: {} can't delete a group: {} not belong to his/her course", user, id);
        throw new IllegalCRUDOperationException("Teacher can't delete a group not belong to his/her course");
      }
    }
    final boolean result = baseMapper.deleteById(id) == 1;
    log.info("User: {} delete a course-group: {}", user != null ? user : "null", courseGroup);
    // update course-student's groupId, set it to null
    if (isStudent) {
      final CourseStudent foundCourseStudent = courseStudentMapper.selectOne(new QueryWrapper<>(new CourseStudent())
        .eq(CourseStudent.COL_STUDENT_ID, user.getId())
        .eq(CourseStudent.COL_COURSE_ID, course.getId()));
      final boolean updateResult = courseStudentMapper.updateById(foundCourseStudent.setGroupId(null)) == 1;
      if (!updateResult) {
        log.error("Update course-student relationship: {} failed anomalously", foundCourseStudent);
        throw new IllegalStateException("Update course-student relationship failed anomalously, please retry");
      }
      log.info("User: {} update a course-student: {}", user, foundCourseStudent);
    }
    return result;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<CompleteGroup> divideGroups(int courseId, GroupingMethodEnum groupingMethod, int expectGroupSize) {
    final Course course = courseMapper.selectById(courseId);
    if (course == null) {
      log.error("Can't divide groups for non-exited course");
      throw new NotFoundException("Can't divide groups for non-exited course");
    }
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      if (!course.getTeacherId().equals(user.getId())) {
        log.warn("Teacher: {} can't divide groups for not-own course: {}", user, course);
        throw new IllegalCRUDOperationException("Teacher can't divide groups for not-own course");
      }
    }
    courseMapper.updateById(course.setGroupSize(expectGroupSize).setGroupingMethod(groupingMethod));
    if (groupingMethod.equals(GroupingMethodEnum.AVERAGE)) {
      return averageGroupingAccordingToStudentNumber(course);
    }
    return Collections.emptyList();
  }

  List<CompleteGroup> averageGroupingAccordingToStudentNumber(Course course) {
    final List<CourseStudent> courseStudents = courseStudentMapper.selectList(new QueryWrapper<>(new CourseStudent())
      .eq(CourseStudent.COL_COURSE_ID, course.getId())
      .orderBy(true, true, CourseStudent.COL_STUDENT_NUMBER));
    final List<List<CourseStudent>> partitionStudents = ListUtils.partition(courseStudents, course.getGroupSize());
    final List<CompleteGroup> result = new ArrayList<>(course.getGroupSize());
    for (int i = 0; i < partitionStudents.size(); i++) {
      final CourseGroup group = CourseGroup.builder()
        .groupName(String.format("%s-第%d小组", course.getCourseName(), i))
        .courseId(course.getId())
        .createTime(new Date())
        .updateTime(new Date())
        .build();
      baseMapper.insert(group);
      partitionStudents.get(i).forEach(student -> courseStudentMapper.updateById(student
        .setUpdateTime(new Date())
        .setGroupId(group.getId())));
      result.add(new CompleteGroup(group, partitionStudents.get(i)));
    }
    return result;
  }
}
