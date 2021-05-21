package cn.edu.shu.pourrfot.server.service;

import cn.edu.shu.pourrfot.server.enums.GroupingMethodEnum;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.model.dto.CompleteGroup;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

  /**
   * divide groups in the course with #courseId by specific grouping method; update the course's #groupingMethod and #groupSize
   *
   * @param courseId        course id
   * @param groupingMethod  grouping method
   * @param expectGroupSize expected group size
   * @return divided groups
   */
  List<CompleteGroup> divideGroups(int courseId, GroupingMethodEnum groupingMethod, int expectGroupSize);
}
