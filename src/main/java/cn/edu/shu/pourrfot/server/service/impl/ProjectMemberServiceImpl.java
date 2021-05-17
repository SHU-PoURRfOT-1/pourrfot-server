package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.helper.PageHelper;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.model.ProjectMember;
import cn.edu.shu.pourrfot.server.model.dto.ProjectMemberUser;
import cn.edu.shu.pourrfot.server.model.dto.SimpleUser;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMemberMapper;
import cn.edu.shu.pourrfot.server.service.ProjectMemberService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author spencercjh
 */
@Slf4j
@Service
public class ProjectMemberServiceImpl extends ServiceImpl<ProjectMemberMapper, ProjectMember> implements ProjectMemberService {
  @Autowired
  private ProjectMapper projectMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @Override
  public Page<ProjectMemberUser> page(Page<ProjectMemberUser> page, Wrapper<ProjectMemberUser> queryWrapper) {
    final int projectId = queryWrapper.getEntity().getProjectId();
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    final boolean notAdminUser = user != null && (user.getRole().equals(RoleEnum.student) ||
      user.getRole().equals(RoleEnum.teacher));
    if (notAdminUser) {
      final Set<Integer> projectIdSet = baseMapper.selectList(new QueryWrapper<>(new ProjectMember()
        .setUserId(user.getId())))
        .stream()
        .map(ProjectMember::getProjectId)
        .collect(Collectors.toSet());
      if (!projectIdSet.contains(projectId)) {
        log.warn("User: {} can't access the project-members in project: {} which isn't belong his/her", user, projectId);
        throw new IllegalCRUDOperationException("User can't access the project-members in project which isn't belong his/her");
      }
    }
    return PageHelper.manuallyPage(baseMapper.selectMemberUsersInOneProject(projectId), page);
  }

  @Override
  public ProjectMemberUser getProjectMemberUserById(int projectMemberId) {
    final ProjectMemberUser found = baseMapper.selectOneProjectMemberUser(projectMemberId);
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    final boolean notAdminUser = user != null && (user.getRole().equals(RoleEnum.student) ||
      user.getRole().equals(RoleEnum.teacher));
    if (notAdminUser) {
      if (!found.getUserId().equals(user.getId())) {
        log.warn("User: {} can't access the project-members in project: {} which isn't belong his/her", user, found.getProjectId());
        throw new IllegalCRUDOperationException("User can't access the project-members in project which isn't belong his/her");
      }
    }
    return found;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(ProjectMember entity) {
    final Project foundProject = projectMapper.selectById(entity.getProjectId());
    if (foundProject == null) {
      final NotFoundException e = new NotFoundException("Can't create a project-member because the project doesn't exist");
      log.error("Can't create a project-member because the project doesn't exist: {}", entity, e);
      throw e;
    }
    final PourrfotUser foundUser = pourrfotUserMapper.selectById(entity.getUserId());
    if (foundUser == null) {
      final NotFoundException e = new NotFoundException("Can't create a project-member because the user doesn't exist");
      log.error("Can't create a project-member because the user doesn't exist: {}", entity, e);
      throw e;
    }
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      final Project project = projectMapper.selectById(entity.getProjectId());
      if (!project.getOwnerId().equals(user.getId())) {
        log.warn("Teacher: {} can't create a project-member for project: {} not belong to his/her", user, project);
        throw new IllegalCRUDOperationException("Teacher can't create a project-member for project not belong to his/her");
      }
    }
    log.info("User: {} create a project-member: {}", user, entity);
    return baseMapper.insert(entity
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(ProjectMember entity) {
    final ProjectMember found = baseMapper.selectById(entity.getId());
    if (found == null) {
      final NotFoundException e = new NotFoundException("Can't update the project-member because the project-member doesn't exist");
      log.error("Can't update a non-existed project-member: {}", entity, e);
      throw e;
    }
    if (!found.getProjectId().equals(entity.getProjectId()) ||
      !found.getUserId().equals(entity.getUserId())) {
      final IllegalCRUDOperationException e = new IllegalCRUDOperationException("Can't update the project-member's immutable fields");
      log.warn("Can't update the project-member's immutable fields: {}", entity);
      throw e;
    }
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      final Project project = projectMapper.selectById(entity.getProjectId());
      if (!project.getOwnerId().equals(user.getId())) {
        log.warn("Teacher: {} can't update a project-member for project: {} not belong to his/her", user, project);
        throw new IllegalCRUDOperationException("Teacher can't update a project-member for project not belong to his/her");
      }
    }
    log.info("User: {} update a project-member: {}", user, entity);
    return baseMapper.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime())) == 1;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean removeById(Serializable id) {
    final ProjectMember found = baseMapper.selectById(id);
    if (found == null) {
      return false;
    }
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      final Project project = projectMapper.selectById(found.getProjectId());
      if (!project.getOwnerId().equals(user.getId())) {
        log.warn("Teacher: {} can't delete a project-member for project: {} not belong to his/her", user, project);
        throw new IllegalCRUDOperationException("Teacher can't delete a project-member for project not belong to his/her");
      }
    }
    log.info("User: {} delete a project-member: {}", user, found);
    return baseMapper.deleteById(id) == 1;
  }
}
