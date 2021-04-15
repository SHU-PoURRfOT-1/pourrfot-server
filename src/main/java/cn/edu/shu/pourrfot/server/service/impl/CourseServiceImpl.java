package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.service.CourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author spencercjh
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
  @Override
  public boolean save(Course entity) {
    return super.save(entity
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis())));
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(Course entity) {
    final Course found = super.getById(entity.getId());
    return super.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime()));
  }
}
