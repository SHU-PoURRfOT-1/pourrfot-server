package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.CourseService;
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
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(Course entity) {
    final PourrfotUser teacher = pourrfotUserMapper.selectById(entity.getTeacherId());
    if (teacher == null || !RoleEnum.teacher.equals(teacher.getRole())) {
      final NotFoundException e = new NotFoundException("Can't create the course with non-existed teacher");
      log.error("Can't save a course because the teacher doesn't exist: {}", entity, e);
      throw e;
    }
    return super.save(entity
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis())));
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(Course entity) {
    final Course found = super.getById(entity.getId());
    if (found == null) {
      final NotFoundException e = new NotFoundException("Can't update the course because not found the course");
      log.error("Can't update a non-existed course: {}", entity, e);
      throw e;
    }
    if (!entity.getTeacherId().equals(found.getTeacherId())) {
      final IllegalCRUDOperationException e = new IllegalCRUDOperationException("Can't modify the group's teacher");
      log.warn("Can't update a course's immutable fields: {}", entity, e);
      throw e;
    }
    return super.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime()));
  }
}
