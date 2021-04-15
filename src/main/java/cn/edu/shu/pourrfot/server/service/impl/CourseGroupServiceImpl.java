package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.repository.CourseGroupMapper;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.service.CourseGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author spencercjh
 */
@Service
public class CourseGroupServiceImpl extends ServiceImpl<CourseGroupMapper, CourseGroup> implements CourseGroupService {
  @Autowired
  private CourseMapper courseMapper;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(CourseGroup entity) {
    final boolean saveResult = baseMapper.insert(entity
      .setGroupName(StringUtils.isNotBlank(entity.getGroupName()) ? entity.getGroupName().trim() : "")
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
    boolean updateResult = true;
    if (saveResult && StringUtils.isBlank(entity.getGroupName())) {
      final Course course = courseMapper.selectById(entity.getCourseId());
      if (course == null) {
        throw new NotFoundException("Can't create a group with a non-existed course");
      }
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
      throw new NotFoundException("Can't update the group because not found the group");
    }
    if (!entity.getCourseId().equals(found.getCourseId())) {
      throw new IllegalCRUDOperationException("Can't modify the group's course");
    }
    return super.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime()));
  }
}
