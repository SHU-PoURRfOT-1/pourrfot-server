package cn.edu.shu.pourrfot.server.repository;

import cn.edu.shu.pourrfot.server.model.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author spencercjh
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
  /**
   * select by student id with page
   *
   * @param startOffset start offset
   * @param rowCount    row count
   * @param studentId   student id
   * @return courses
   */
  List<Course> selectByStudentIdWithPage(@Param("startOffset") long startOffset, @Param("rowCount") long rowCount,
                                         @Param("studentId") int studentId);

  /**
   * select by student id
   *
   * @param studentId student id
   * @return courses
   */
  List<Course> selectByStudentId(@Param("studentId") int studentId);

  /**
   * count total of {@link #selectByStudentIdWithPage(long, long, int)}
   *
   * @param studentId studentId
   * @return total
   */
  int countByStudentId(@Param("studentId") int studentId);
}
