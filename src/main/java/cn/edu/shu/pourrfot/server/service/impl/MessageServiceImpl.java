package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.Message;
import cn.edu.shu.pourrfot.server.model.InboxMessage;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.repository.InboxMessageMapper;
import cn.edu.shu.pourrfot.server.repository.MessageMapper;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.MessageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

/**
 * @author spencercjh
 */
@Slf4j
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private InboxMessageMapper inboxMessageMapper;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(Message entity) {
    final PourrfotUser sender = pourrfotUserMapper.selectById(entity.getSender());
    if (sender == null) {
      final NotFoundException e = new NotFoundException("Can't create a message with non-existed sender");
      log.error("Can't save a message because the sender doesn't exist: {}", entity, e);
      throw e;
    }
    final PourrfotUser receiver = pourrfotUserMapper.selectById(entity.getReceiver());
    if (receiver == null) {
      final NotFoundException e = new NotFoundException("Can't create a message with non-existed receiver");
      log.error("Can't save a message because the receiver doesn't exist: {}", entity, e);
      throw e;
    }
    final boolean insertMessageResult = baseMapper.insert(entity
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
    // add messages to inbox
    final boolean addToInboxResult = inboxMessageMapper.insert(InboxMessage.builder()
      .messageId(entity.getId())
      .haveRead(false)
      .sender(entity.getSender())
      .receiver(entity.getReceiver())
      .createTime(new Date(System.currentTimeMillis()))
      .updateTime(new Date(System.currentTimeMillis()))
      .build()) == 1;
    return insertMessageResult && addToInboxResult;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean removeById(Serializable id) {
    // remove messages in the inbox
    final QueryWrapper<InboxMessage> inboxQueryWrapper = new QueryWrapper<>(new InboxMessage())
      .eq(InboxMessage.COL_MESSAGE_ID, id);
    final int inboxCount = inboxMessageMapper.selectCount(inboxQueryWrapper);
    final boolean withdrawMessageResult = inboxCount == inboxMessageMapper.delete(inboxQueryWrapper);
    log.info("Delete {} message inbox before removing the message: {}", inboxCount, id);
    return withdrawMessageResult && baseMapper.deleteById(id) == 1;
  }
}
