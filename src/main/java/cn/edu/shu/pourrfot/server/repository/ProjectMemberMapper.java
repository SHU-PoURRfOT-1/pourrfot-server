package cn.edu.shu.pourrfot.server.repository;

import cn.edu.shu.pourrfot.server.model.ProjectMember;
import cn.edu.shu.pourrfot.server.model.dto.ProjectMemberUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author spencercjh
 */
@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
  /**
   * select user info from project-member joining user by project id
   *
   * @param projectId project id
   * @return users
   */
  List<ProjectMemberUser> selectMemberUsersInOneProject(@Param("projectId") int projectId);

  /**
   * select user info from project-member joining user by project-member id
   *
   * @param id project member id
   * @return user
   */
  ProjectMemberUser selectOneProjectMemberUser(@Param("id") int id);
}
