package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.model.ProjectMember;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMemberMapper;
import cn.edu.shu.pourrfot.server.service.ProjectMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(ProjectMember entity) {
    checkAssociatedResource(entity);
    return super.save(entity
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis())));
  }

  private void checkAssociatedResource(ProjectMember entity) {
    final Project foundProject = projectMapper.selectById(entity.getProjectId());
    if (foundProject == null) {
      final NotFoundException e = new NotFoundException("Can't save a project-member because the project doesn't exist");
      log.error("Can't save/update a project-member because the project doesn't exist: {}", entity, e);
      throw e;
    }
    final PourrfotUser foundUser = pourrfotUserMapper.selectById(entity.getUserId());
    if (foundUser == null) {
      final NotFoundException e = new NotFoundException("Can't save a project-member because the user doesn't exist");
      log.error("Can't save/update a project-member because the user doesn't exist: {}", entity, e);
      throw e;
    }
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(ProjectMember entity) {
    final ProjectMember found = super.getById(entity.getId());
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
    return super.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime()));
  }
}
