package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.dto.SimpleUser;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
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
      final E resultPage = super.page(page, wrapper);
      final Set<Integer> studentCourseIdSet = baseMapper.selectByStudentId(user.getId())
        .stream()
        .map(Course::getId)
        .collect(Collectors.toSet());
      resultPage.setRecords(resultPage.getRecords()
        .stream()
        .filter(course -> studentCourseIdSet.contains(course.getId()))
        .collect(Collectors.toList()));
      resultPage.setTotal(studentCourseIdSet.size());
      return resultPage;
    } else if (user == null || user.getRole().equals(RoleEnum.admin)) {
      return super.page(page, wrapper);
    } else {
      throw new IllegalArgumentException("Invalid CourseService#Page state");
    }
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(Course entity) {
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    // only admin and teacher user can access this method
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      if (!entity.getTeacherId().equals(user.getId())) {
        log.warn("Teacher user: {} is creating a not-own course: {}", user, entity);
        throw new IllegalCRUDOperationException("Teacher user can't create a not-own course");
      }
    }
    final PourrfotUser teacher = pourrfotUserMapper.selectById(entity.getTeacherId());
    if (teacher == null || !RoleEnum.teacher.equals(teacher.getRole())) {
      final NotFoundException e = new NotFoundException("Can't create the course with non-existed teacher");
      log.error("Can't save a course because the teacher doesn't exist: {}", entity, e);
      throw e;
    }
    return baseMapper.insert(entity
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
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
    return baseMapper.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime())) == 1;
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
    return baseMapper.deleteById(id) == 1;
  }
}
