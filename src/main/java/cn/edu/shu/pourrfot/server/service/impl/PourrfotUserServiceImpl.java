package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.PourrfotUserService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author spencercjh
 */
@Slf4j
@Service
public class PourrfotUserServiceImpl extends ServiceImpl<PourrfotUserMapper, PourrfotUser> implements PourrfotUserService {
  @Override
  public <E extends IPage<PourrfotUser>> E page(E page, Wrapper<PourrfotUser> queryWrapper) {
    final E result = super.page(page, queryWrapper);
    result.getRecords().forEach(record -> record.setPassword("******"));
    return result;
  }

  @Override
  public boolean save(PourrfotUser entity) {
    if (StringUtils.isBlank(entity.getPassword())) {
      entity.setPassword(entity.getUsername());
      log.warn("User: {} 's password is empty, use username to replace", entity.getUsername());
    }
    return super.save(entity
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis())));
  }

  @Override
  public boolean updateById(PourrfotUser entity) {
    final PourrfotUser found = super.getById(entity.getId());
    if (found == null) {
      final NotFoundException e = new NotFoundException("Can't update the user because not found the user");
      log.error("Can't update a non-existed user: {}", entity, e);
      throw e;
    }
    if (!entity.getRole().equals(found.getRole())) {
      final IllegalCRUDOperationException e = new IllegalCRUDOperationException("Can't modify the user's role");
      log.warn("Can't update a user's immutable fields: {}", entity, e);
      throw e;
    }
    return super.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime()));
  }
}
