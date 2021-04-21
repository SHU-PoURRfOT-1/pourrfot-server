package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMapper;
import cn.edu.shu.pourrfot.server.service.ProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author spencercjh
 */
@Service
@Slf4j
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @Override
  public boolean save(Project entity) {
    checkAssociatedResource(entity);
    return super.save(entity
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis())));
  }

  @Override
  public boolean updateById(Project entity) {
    final Project found = super.getById(entity.getId());
    if (found == null) {
      final NotFoundException e = new NotFoundException("Can't update the project because the project doesn't exist");
      log.error("Can't update a non-existed project: {}", entity, e);
      throw e;
    }
    checkAssociatedResource(entity);
    return super.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime()));
  }

  private void checkAssociatedResource(Project entity) {
    if (entity.getOwnerId() != null) {
      final PourrfotUser foundOwner = pourrfotUserMapper.selectById(entity.getOwnerId());
      if (foundOwner == null) {
        final NotFoundException e = new NotFoundException("Can't save a project because the owner doesn't exist");
        log.error("Can't save/update a project because the owner doesn't exist: {}", entity, e);
        throw e;
      }
    }
  }
}
