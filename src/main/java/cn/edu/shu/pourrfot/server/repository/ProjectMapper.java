package cn.edu.shu.pourrfot.server.repository;

import cn.edu.shu.pourrfot.server.model.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author spencercjh
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
  /**
   * select by user id joining project_member
   *
   * @param userId user id
   * @return projects
   */
  List<Project> selectByUserId(@Param("userId") int userId);
}
