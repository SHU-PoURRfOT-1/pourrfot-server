package cn.edu.shu.pourrfot.server.service;

import cn.edu.shu.pourrfot.server.model.CourseStudent;
import cn.edu.shu.pourrfot.server.model.dto.CompleteCourseStudent;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author spencercjh
 */
public interface CourseStudentService extends IService<CourseStudent> {

  Page<CompleteCourseStudent> pageCompleteCourseStudents(Page<CourseStudent> page, Wrapper<CourseStudent> queryWrapper);

  CompleteCourseStudent getCompleteCourseStudentById(int id);
}
