package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.model.InboxMessage;
import cn.edu.shu.pourrfot.server.model.dto.SingleMessage;
import cn.edu.shu.pourrfot.server.repository.InboxMessageMapper;
import cn.edu.shu.pourrfot.server.service.InboxMessageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author spencercjh
 */
@Service
public class InboxMessageServiceImpl extends ServiceImpl<InboxMessageMapper, InboxMessage> implements InboxMessageService {

  @Transactional(rollbackFor = Exception.class)
  @Override
  public Page<SingleMessage> messagePage(Integer sender, Integer receiver, String title, Boolean isUrgent,
                                         Boolean isRegular, Boolean haveRead, int current, int size) {
    final int total = baseMapper.countMessagesByCondition(sender, receiver, title, isUrgent, isRegular, haveRead);
    // optimize page
    current = Math.min(current, (total + size - 1) / size);
    return new Page<SingleMessage>(current, size,
      total, true)
      .setRecords(baseMapper.selectMessagesByCondition((long) (current - 1) * size, size,
        sender, receiver, title, isUrgent, isRegular, haveRead));
  }

  @Override
  public SingleMessage getMessageByInboxMessageId(Integer id) {
    return baseMapper.selectMessageByInboxMessageId(id);
  }
}
