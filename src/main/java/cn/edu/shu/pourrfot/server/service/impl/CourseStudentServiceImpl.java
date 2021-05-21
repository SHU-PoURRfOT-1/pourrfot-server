package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.GroupingMethodEnum;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.*;
import cn.edu.shu.pourrfot.server.model.dto.CompleteCourseStudent;
import cn.edu.shu.pourrfot.server.model.dto.SimpleUser;
import cn.edu.shu.pourrfot.server.repository.CourseGroupMapper;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.CourseStudentMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.CourseStudentService;
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
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author spencercjh
 */
@Slf4j
@Service
public class CourseStudentServiceImpl extends ServiceImpl<CourseStudentMapper, CourseStudent> implements CourseStudentService {
  @Autowired
  private CourseMapper courseMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private CourseGroupMapper courseGroupMapper;

  @Override
  public Page<CompleteCourseStudent> pageCompleteCourseStudents(Page<CourseStudent> page, Wrapper<CourseStudent> queryWrapper) {
    final Page<CourseStudent> result = page(page, queryWrapper);
    return new Page<CompleteCourseStudent>(result.getCurrent(), result.getSize(), result.getTotal())
      .setRecords(result.getRecords()
        .stream()
        .map(courseStudent -> CompleteCourseStudent.of(courseStudent,
          courseMapper.selectById(courseStudent.getCourseId()),
          courseGroupMapper.selectById(courseStudent.getGroupId()))).collect(Collectors.toList()));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <E extends IPage<CourseStudent>> E page(E page, Wrapper<CourseStudent> queryWrapper) {
    // there is an existed condition eq(course_id,id) in the wrapper
    final int courseId = queryWrapper.getEntity().getCourseId();
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    // student users can only access themselves
    if (user != null && user.getRole().equals(RoleEnum.student)) {
      final CourseStudent courseStudent = baseMapper.selectOne(new QueryWrapper<>(new CourseStudent())
        .eq(CourseStudent.COL_COURSE_ID, courseId)
        .eq(CourseStudent.COL_STUDENT_ID, user.getId()));
      if (courseStudent == null) {
        return (E) page.setTotal(0);
      }
      return (E) page.setTotal(1).setRecords(Collections.singletonList(courseStudent));
    }
    // teacher user can only view the students in their own courses
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      final Course course = courseMapper.selectById(courseId);
      if (!course.getTeacherId().equals(user.getId())) {
        log.warn("Teacher: {} can't access the students in the course: {} which isn't belong his/her", user, course);
        throw new IllegalCRUDOperationException("Teacher can't access the students in the course which isn't belong his/her");
      }
    }
    return super.page(page, queryWrapper);
  }

  @Override
  public CompleteCourseStudent getCompleteCourseStudentById(int id) {
    final CourseStudent courseStudent = getById(id);
    if (courseStudent == null) {
      return null;
    }
    return CompleteCourseStudent.of(courseStudent,
      courseMapper.selectById(courseStudent.getCourseId()),
      courseGroupMapper.selectById(courseStudent.getGroupId()));
  }

  @Override
  public CourseStudent getById(Serializable id) {
    final CourseStudent found = baseMapper.selectById(id);
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user != null && user.getRole().equals(RoleEnum.student)) {
      if (!found.getStudentId().equals(user.getId())) {
        log.warn("Student: {} can't access the student: {} in the course: {} which isn't belong his/her", user, id, found.getId());
        throw new IllegalCRUDOperationException("Student can't access the student in the course which isn't belong his/her");
      }
    } else if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      final Course course = courseMapper.selectById(found.getCourseId());
      if (!course.getTeacherId().equals(user.getId())) {
        log.warn("Teacher: {} can't access the student: {} in the course: {} which isn't belong his/her", user, id, course);
        throw new IllegalCRUDOperationException("Teacher can't access the student in the course which isn't belong his/her");
      }
    }
    return found;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(CourseStudent courseStudent) {
    final AssociatedResource associatedResource = checkResource(courseStudent);
    // ensure unique
    if (baseMapper.selectCount(new QueryWrapper<>(new CourseStudent()
      .setCourseId(courseStudent.getCourseId())
      .setStudentId(courseStudent.getStudentId()))) > 0) {
      log.error("course-student should be unique: {} but duplicated", courseStudent);
      throw new IllegalCRUDOperationException("course-student should be unique but duplicated");
    }
    final boolean result = baseMapper.insert(courseStudent
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))
      .setStudentName(associatedResource.student.getNickname())
      .setStudentNumber(associatedResource.student.getUsername())) == 1;
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    log.info("User: {} create a course-student: {}", user != null ? user : "null", courseStudent);
    return result;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(CourseStudent courseStudent) {
    final CourseStudent found = baseMapper.selectById(courseStudent.getId());
    if (found == null) {
      final NotFoundException e = new NotFoundException("Can't update a non-existed course-student");
      log.error("Can't update a non-existed course-student: {}", courseStudent, e);
      throw e;
    }
    if (!found.getCourseId().equals(courseStudent.getCourseId()) ||
      !found.getStudentNumber().equals(courseStudent.getStudentNumber()) ||
      !found.getStudentName().equals(courseStudent.getStudentName()) ||
      !found.getStudentId().equals(courseStudent.getStudentId())) {
      final IllegalCRUDOperationException e = new IllegalCRUDOperationException("Can't update a course-student's immutable fields");
      log.warn("Can't update a course-student's immutable fields: {}", courseStudent, e);
      throw e;
    }
    final AssociatedResource associatedResource = checkResource(courseStudent);
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    final boolean notStudent = user != null && (user.getRole().equals(RoleEnum.teacher) || user.getRole().equals(RoleEnum.admin));
    if (notStudent) {
      if (!courseStudent.getDetailScore().equals(found.getDetailScore()) &&
        !courseStudent.getDetailScore().isEmpty()) {
        courseStudent.setTotalScore(calculateScore(courseStudent, associatedResource.course, user));
      }
      // Unrestricted group modification for non-student users
    }
    if (user != null && user.getRole().equals(RoleEnum.student)) {
      courseStudent.setTotalScore(found.getTotalScore()).setDetailScore(found.getDetailScore());
      if (!courseStudent.getGroupId().equals(found.getGroupId())) {
        // check group restrictions
        checkGroupRestrictions(associatedResource.course, associatedResource.group, user);
      }
    }
    final boolean result = baseMapper.updateById(courseStudent
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime())) == 1;
    log.info("User: {} update a course-student: {}", user != null ? user : "null", courseStudent);
    return result;
  }

  private void checkGroupRestrictions(Course course, CourseGroup group, SimpleUser user) {
    if (!course.getGroupingMethod().equals(GroupingMethodEnum.FREE)) {
      log.warn("Student: {} can't join any group by own because the course is not free for grouping: {}", user,
        course.getGroupingMethod());
      throw new IllegalCRUDOperationException("Student can't join any group by own because the course is not free for grouping");
    }
    final int groupStudentCount = baseMapper.selectCount(new QueryWrapper<>(new CourseStudent())
      .eq(CourseStudent.COL_GROUP_ID, group.getId()));
    if (course.getGroupSize() - groupStudentCount <= 0) {
      log.warn("Student: {} is joining a full(max: {}) group: {} with size: {}", user, course.getGroupSize(),
        group, groupStudentCount);
      throw new IllegalCRUDOperationException("The group is full, please contact the teacher if you want to join");
    }
  }

  private long calculateScore(CourseStudent courseStudent, Course course, SimpleUser user) {
    final Map<String, ScoreItem> courseScoreStructure = course.getScoreStructure()
      .stream()
      .map(o -> {
        if (o instanceof ScoreItem) {
          return ((ScoreItem) o);
        }
        @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) o;
        return getScoreItem(map);
      })
      .collect(Collectors.toMap(ScoreItem::getName, Function.identity()));
    double total = 0;
    for (Object o : courseStudent.getDetailScore()) {
      ScoreItem scoreItem;
      if (o instanceof ScoreItem) {
        scoreItem = (ScoreItem) o;
      } else {
        @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) o;
        scoreItem = getScoreItem(map);
      }
      if (!courseScoreStructure.containsKey(scoreItem.getName())) {
        log.warn("Teacher: {} update student: {} 's score with not-existed score item: {}", user, courseStudent,
          scoreItem);
        throw new NotFoundException("Not existed score item");
      }
      total += scoreItem.getScore() * scoreItem.getWeight();
    }
    final NumberFormat numberFormat = NumberFormat.getInstance();
    numberFormat.setMaximumFractionDigits(2);
    total = Double.parseDouble(numberFormat.format(total));
    return (long) (total * 100L);
  }

  private ScoreItem getScoreItem(Map<String, Object> map) {
    final Object score = map.get("score");
    final Object weight = map.get("weight");
    return new ScoreItem(((String) map.get("name")),
      score instanceof String ? Double.parseDouble((String) score) : (Double) score,
      weight instanceof String ? Double.parseDouble((String) weight) : (Double) weight,
      ((String) map.get("description")));
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean removeById(Serializable id) {
    final CourseStudent courseStudent = baseMapper.selectById(id);
    if (courseStudent == null) {
      return false;
    }
    final Course course = courseMapper.selectById(courseStudent.getCourseId());
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    // teacher can't delete not-own student
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      if (!course.getTeacherId().equals(user.getId())) {
        log.error("Teacher: {} can't delete course-student: {} with a not-own course: {}", user, id, course.getId());
        throw new IllegalCRUDOperationException("Teacher can't delete the course-student with a not-own course");
      }
    }
    final boolean result = baseMapper.deleteById(id) == 1;
    log.info("User: {} delete a course-student: {}", user != null ? user : "null", id);
    return result;
  }

  private AssociatedResource checkResource(CourseStudent courseStudent) {
    final Course foundCourse = courseMapper.selectById(courseStudent.getCourseId());
    if (foundCourse == null) {
      final NotFoundException e = new NotFoundException("Can't create a course-student with a non-exist course");
      log.error("Save/Update course student entity failed with non-exist course: {}", courseStudent, e);
      throw e;
    }
    final PourrfotUser foundUser = pourrfotUserMapper.selectById(courseStudent.getStudentId());
    if (foundUser == null || !foundUser.getRole().equals(RoleEnum.student)) {
      final NotFoundException e = new NotFoundException("Can't create a course-student with a non-exist student");
      log.error("Save/Update course student entity failed with non-exist student: {}", courseStudent, e);
      throw e;
    }
    CourseGroup foundGroup = null;
    if (courseStudent.getGroupId() != null) {
      foundGroup = courseGroupMapper.selectById(courseStudent.getGroupId());
      if (foundGroup == null) {
        final NotFoundException e = new NotFoundException("Can't create a course-student with a non-exist group");
        log.error("Save/Update course student entity failed with non-exist group: {}", courseStudent, e);
        throw e;
      }
      if (!foundGroup.getCourseId().equals(foundCourse.getId())) {
        log.warn("Can't create a course-student: {} with a group whose course: {} is different from the student's course: {}",
          courseStudent, foundGroup.getCourseId(), foundCourse.getId());
        throw new IllegalCRUDOperationException("Can't create a course-student with a group whose course is different from the student's course");
      }
    }
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user != null && user.getRole().equals(RoleEnum.student)) {
      // check limited group
      final boolean isLimitedGroupingMethod = foundCourse.getGroupingMethod().equals(GroupingMethodEnum.NOT_GROUPING) ||
        foundCourse.getGroupingMethod().equals(GroupingMethodEnum.STRICT_CONTROLLED);
      if (foundGroup != null && isLimitedGroupingMethod) {
        log.warn("Student: {} can't update his/her group because the course's grouping method is limited: {}",
          user, foundCourse.getGroupingMethod());
        throw new IllegalCRUDOperationException("Student can't update his/her group because the course's grouping method is limited");
      }
    } else if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      // not own course
      if (!foundCourse.getTeacherId().equals(user.getId())) {
        log.error("Teacher: {} can't access course-student: {} with a not-own course: {}", user, courseStudent, foundCourse);
        throw new IllegalCRUDOperationException("Teacher can't access the course-student with a not-own course");
      }
    }
    return new AssociatedResource(foundCourse, foundUser, foundGroup);
  }

  private static class AssociatedResource {
    private final Course course;
    private final PourrfotUser student;
    private final CourseGroup group;

    private AssociatedResource(Course course, PourrfotUser student, CourseGroup group) {
      this.course = course;
      this.student = student;
      this.group = group;
    }
  }
}
