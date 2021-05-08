package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.StudentTransaction;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.repository.StudentTransactionMapper;
import cn.edu.shu.pourrfot.server.service.StudentTransactionService;
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
public class StudentTransactionServiceImpl extends ServiceImpl<StudentTransactionMapper, StudentTransaction> implements StudentTransactionService {
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(StudentTransaction entity) {
    checkAssociatedResource(entity);
    return baseMapper.insert(entity
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean updateById(StudentTransaction entity) {
    final StudentTransaction found = super.getById(entity.getId());
    if (found == null) {
      final NotFoundException e = new NotFoundException("Can't update the student-transaction because the student-transaction doesn't exist");
      log.error("Can't update a non-existed student-transaction: {}", entity, e);
      throw e;
    }
    checkAssociatedResource(entity);
    if (!found.getSender().equals(entity.getSender())) {
      final IllegalCRUDOperationException e = new IllegalCRUDOperationException("Can't update the student-transaction's immutable fields");
      log.warn("Can't update the student-transaction's immutable fields: {}", entity);
      throw e;
    }
    return super.updateById(entity
      .setCreateTime(found.getCreateTime())
      .setUpdateTime(found.getUpdateTime()));
  }

  private void checkAssociatedResource(StudentTransaction entity) {
    if (pourrfotUserMapper.selectById(entity.getSender()) == null) {
      final NotFoundException e = new NotFoundException("Can't save a student-transaction because the sender doesn't exist");
      log.error("Can't save/update a student-transaction because the sender doesn't exist: {}", entity, e);
      throw e;
    }
    if (pourrfotUserMapper.selectById(entity.getReceiver()) == null) {
      final NotFoundException e = new NotFoundException("Can't save a student-transaction because the receiver doesn't exist");
      log.error("Can't save/update a student-transaction because the receiver doesn't exist: {}", entity, e);
      throw e;
    }
  }
}
