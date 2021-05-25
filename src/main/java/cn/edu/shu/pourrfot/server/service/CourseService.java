package cn.edu.shu.pourrfot.server.service;

import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.dto.StudentCourse;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author spencercjh
 */
public interface CourseService extends IService<Course> {
  /**
   * student course page
   *
   * @param page    page
   * @param wrapper wrapper
   * @return student course page
   */
  Page<StudentCourse> studentCoursePage(Page<Course> page, Wrapper<Course> wrapper);
}
