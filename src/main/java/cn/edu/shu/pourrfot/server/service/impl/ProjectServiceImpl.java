package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.ProjectMemberRoleEnum;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.helper.PageHelper;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.model.ProjectMember;
import cn.edu.shu.pourrfot.server.model.dto.SimpleUser;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMemberMapper;
import cn.edu.shu.pourrfot.server.service.ProjectService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author spencercjh
 */
@Service
@Slf4j
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private ProjectMemberMapper projectMemberMapper;

  @Override
  public <E extends IPage<Project>> E page(E page, Wrapper<Project> queryWrapper) {
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    final boolean notAdminUser = user != null && (user.getRole().equals(RoleEnum.student) ||
      user.getRole().equals(RoleEnum.teacher));
    if (notAdminUser) {
      final Set<Integer> projectIdSet = baseMapper.selectByUserId(user.getId()).stream()
        .map(Project::getId)
        .collect(Collectors.toSet());
      final List<Project> userProjects = baseMapper.selectList(queryWrapper)
        .stream()
        .filter(project -> projectIdSet.contains(project.getId()))
        .collect(Collectors.toList());
      return PageHelper.manuallyPage(userProjects, page);
    }
    return super.page(page, queryWrapper);
  }

  @Override
  public Project getById(Serializable id) {
    final Project project = baseMapper.selectById(id);
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    final boolean notAdminUser = user != null && (user.getRole().equals(RoleEnum.student) ||
      user.getRole().equals(RoleEnum.teacher));
    if (notAdminUser) {
      if (project.getOwnerId().equals(user.getId())) {
        return project;
      }
      final Set<Integer> projectIdSet = baseMapper.selectByUserId(user.getId()).stream()
        .map(Project::getId)
        .collect(Collectors.toSet());
      if (!projectIdSet.contains(((int) id))) {
        log.warn("User: {} can't access the project: {} not belong to his/her", user, project);
        throw new IllegalCRUDOperationException("User can't access the project not belong to his/her");
      }
    }
    return project;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(Project project) {
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user == null || user.getRole().equals(RoleEnum.admin)) {
      final PourrfotUser foundOwner = pourrfotUserMapper.selectById(project.getOwnerId());
      if (foundOwner == null) {
        final NotFoundException e = new NotFoundException("Can't create a project because the owner doesn't exist");
        log.error("Can't create a project because the owner doesn't exist: {}", project, e);
        throw e;
      }
      if (foundOwner.getRole().equals(RoleEnum.student)) {
        log.warn("Student: {} can't own projects", foundOwner);
        throw new IllegalCRUDOperationException("Student can't own a project");
      }
    } else {
      // teacher can only create own project
      project.setOwnerId(user.getId());
    }
    final boolean insertProjectResult = baseMapper.insert(project
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
    log.info("User: {} create a project: {}", user, project);
    final ProjectMember ownerMember = ProjectMember.builder()
      .projectId(project.getId())
      .userId(project.getOwnerId())
      .roleName(ProjectMemberRoleEnum.OWNER.name())
      .createTime(new Date(System.currentTimeMillis()))
      .updateTime(new Date(System.currentTimeMillis()))
      .build();
    final boolean insertMemberResult = projectMemberMapper.insert(ownerMember) == 1;
    log.info("User: {} create a project-member: {}", user, ownerMember);
    return insertProjectResult && insertMemberResult;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(Project entity) {
    final Project found = baseMapper.selectById(entity.getId());
    if (found == null) {
      final NotFoundException e = new NotFoundException("Can't update the project because the project doesn't exist");
      log.error("Can't update a non-existed project: {}", entity, e);
      throw e;
    }
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user == null || user.getRole().equals(RoleEnum.admin)) {
      if (!found.getOwnerId().equals(entity.getOwnerId())) {
        log.warn("Can't update a project's immutable fields: {}", entity);
        throw new IllegalCRUDOperationException("Can't modify the project's owner");
      }
    } else {
      // teacher can only update own project
      entity.setOwnerId(user.getId());
    }
    log.info("User: {} update a project: {}", user, entity);
    return baseMapper.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime())) == 1;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean removeById(Serializable id) {
    final Project found = baseMapper.selectById(id);
    if (found == null) {
      return false;
    }
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user != null && user.getRole().equals(RoleEnum.teacher)) {
      if (!found.getOwnerId().equals(user.getId())) {
        log.warn("Teacher user: {} is deleting a not-own project: {}", user, found);
        throw new IllegalCRUDOperationException("Teacher user can't delete a not-own project");
      }
    }

    final boolean deleteProjectResult = baseMapper.deleteById(id) == 1;
    log.info("User: {} delete a project: {}", user, found);
    final int projectMemberCount = projectMemberMapper.selectCount(new QueryWrapper<>(new ProjectMember())
      .eq(ProjectMember.COL_PROJECT_ID, id));
    final int deleteResult = projectMemberMapper.delete(new QueryWrapper<>(new ProjectMember())
      .eq(ProjectMember.COL_PROJECT_ID, id));
    log.info("User: {} delete {} project-members", user, projectMemberCount);
    return deleteProjectResult && projectMemberCount == deleteResult;
  }

}
