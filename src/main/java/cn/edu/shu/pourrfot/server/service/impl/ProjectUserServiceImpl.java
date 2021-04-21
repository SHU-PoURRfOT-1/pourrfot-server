package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.model.ProjectUser;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectUserMapper;
import cn.edu.shu.pourrfot.server.service.ProjectUserService;
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
public class ProjectUserServiceImpl extends ServiceImpl<ProjectUserMapper, ProjectUser> implements ProjectUserService {
  @Autowired
  private ProjectMapper projectMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(ProjectUser entity) {
    checkAssociatedResource(entity);
    return super.save(entity
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis())));
  }

  private void checkAssociatedResource(ProjectUser entity) {
    final Project foundProject = projectMapper.selectById(entity.getProjectId());
    if (foundProject == null) {
      final NotFoundException e = new NotFoundException("Can't save a project-user because the project doesn't exist");
      log.error("Can't save/update a project-user because the project doesn't exist: {}", entity, e);
      throw e;
    }
    final PourrfotUser foundUser = pourrfotUserMapper.selectById(entity.getUserId());
    if (foundUser == null) {
      final NotFoundException e = new NotFoundException("Can't save a project-user because the user doesn't exist");
      log.error("Can't save/update a project-user because the user doesn't exist: {}", entity, e);
      throw e;
    }
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(ProjectUser entity) {
    final ProjectUser found = super.getById(entity.getId());
    if (found == null) {
      final NotFoundException e = new NotFoundException("Can't update the project-user because the project-user doesn't exist");
      log.error("Can't update a non-existed project-user: {}", entity, e);
      throw e;
    }
    if (!found.getProjectId().equals(entity.getProjectId()) ||
      !found.getUserId().equals(entity.getUserId())) {
      final IllegalCRUDOperationException e = new IllegalCRUDOperationException("Can't update the project-user's immutable fields");
      log.warn("Can't update the project-user's immutable fields: {}", entity);
      throw e;
    }
    return super.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime()));
  }
}
