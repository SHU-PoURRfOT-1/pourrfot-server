package cn.edu.shu.pourrfot.server.service;

import cn.edu.shu.pourrfot.server.model.ProjectMember;
import cn.edu.shu.pourrfot.server.model.dto.ProjectMemberUser;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author spencercjh
 */
public interface ProjectMemberService extends IService<ProjectMember> {

  Page<ProjectMemberUser> page(Page<ProjectMemberUser> page, Wrapper<ProjectMemberUser> queryWrapper);

  ProjectMemberUser getProjectMemberUserById(int projectMemberId);
}
