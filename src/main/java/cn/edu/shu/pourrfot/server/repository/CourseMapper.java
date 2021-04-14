package cn.edu.shu.pourrfot.server.repository;

import cn.edu.shu.pourrfot.server.model.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author spencercjh
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
