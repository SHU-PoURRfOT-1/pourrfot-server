package cn.edu.shu.pourrfot.server.service;

import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.model.dto.CompleteGroup;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author spencercjh
 */
public interface CourseGroupService extends IService<CourseGroup> {
  /**
   * page query
   *
   * @param page         page
   * @param queryWrapper wrapper
   * @return group with group's students
   */
  Page<CompleteGroup> page(Page<CourseGroup> page, Wrapper<CourseGroup> queryWrapper);

  /**
   * get by id
   *
   * @param id id
   * @return group with group's students
   */
  CompleteGroup getCompleteGroupById(int id);
}
