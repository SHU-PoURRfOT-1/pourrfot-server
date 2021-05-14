package cn.edu.shu.pourrfot.server.repository;

import cn.edu.shu.pourrfot.server.model.CourseGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author spencercjh
 */
@Mapper
public interface CourseGroupMapper extends BaseMapper<CourseGroup> {
  /**
   * select group by student id and courseId
   *
   * @param studentId studentId
   * @param courseId  courseId
   * @return one group
   */
  CourseGroup selectByStudentIdAndCourseId(@Param("studentId") int studentId, @Param("courseId") int courseId);

  /**
   * select by student id
   *
   * @param studentId studentId
   * @return groups
   */
  List<CourseGroup> selectByStudentId(@Param("studentId") int studentId);
}
