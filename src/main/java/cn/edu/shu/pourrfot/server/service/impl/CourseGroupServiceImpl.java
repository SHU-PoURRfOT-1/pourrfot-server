package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.repository.CourseGroupMapper;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.service.CourseGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author spencercjh
 */
@Slf4j
@Service
public class CourseGroupServiceImpl extends ServiceImpl<CourseGroupMapper, CourseGroup> implements CourseGroupService {
  @Autowired
  private CourseMapper courseMapper;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(CourseGroup entity) {
    final Course course = courseMapper.selectById(entity.getCourseId());
    if (course == null) {
      final NotFoundException e = new NotFoundException("Can't create a group with a non-existed course");
      log.error("Save a course group failed because the course doesn't exist: {}", entity, e);
      throw e;
    }
    final boolean saveResult = baseMapper.insert(entity
      .setGroupName(StringUtils.isNotBlank(entity.getGroupName()) ? entity.getGroupName().trim() : "")
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
    boolean updateResult = true;
    if (saveResult && StringUtils.isBlank(entity.getGroupName())) {
      updateResult = updateById(entity.setGroupName(String.format("%s-第%d小组",
        course.getCourseName(), entity.getId())));
    }
    return saveResult && updateResult;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(CourseGroup entity) {
    final CourseGroup found = super.getById(entity.getId());
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
    return super.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime()));
  }
}
