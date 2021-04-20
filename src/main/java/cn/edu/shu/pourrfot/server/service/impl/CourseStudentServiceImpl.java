package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.model.CourseStudent;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.repository.CourseGroupMapper;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.CourseStudentMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.CourseStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(CourseStudent entity) {
    checkAssociatedResource(entity);
    return baseMapper.insert(entity
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(CourseStudent entity) {
    final CourseStudent found = baseMapper.selectById(entity.getId());
    if (found == null) {
      final NotFoundException e = new NotFoundException("Can't update a non-existed course-student");
      log.error("Can't update a non-existed course-student: {}", entity, e);
      throw e;
    }
    if (!found.getCourseId().equals(entity.getCourseId()) ||
      !found.getStudentName().equals(entity.getStudentName()) ||
      !found.getStudentId().equals(entity.getStudentId())) {
      final IllegalCRUDOperationException e = new IllegalCRUDOperationException("Can't update a course-student's immutable fields");
      log.warn("Can't update a course-student's immutable fields: {}", entity, e);
      throw e;
    }
    checkAssociatedResource(entity);
    return super.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime()));
  }

  private void checkAssociatedResource(CourseStudent entity) {
    final Course foundCourse = courseMapper.selectById(entity.getCourseId());
    if (foundCourse == null) {
      final NotFoundException e = new NotFoundException("Can't create a course-student with a non-exist course");
      log.error("Save course student entity failed with non-exist course: {}", entity, e);
      throw e;
    }
    final PourrfotUser foundUser = pourrfotUserMapper.selectById(entity.getStudentId());
    if (foundUser == null || !foundUser.getRole().equals(RoleEnum.student)) {
      final NotFoundException e = new NotFoundException("Can't create a course-student with a non-exist student");
      log.error("Save course student entity failed with non-exist student: {}", entity, e);
      throw e;
    }
    if (entity.getGroupId() != null) {
      final CourseGroup foundGroup = courseGroupMapper.selectById(entity.getGroupId());
      if (foundGroup == null) {
        final NotFoundException e = new NotFoundException("Can't create a course-student with a non-exist group");
        log.error("Save course student entity failed with non-exist group: {}", entity, e);
        throw e;
      }
    }
  }
}
