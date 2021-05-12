package cn.edu.shu.pourrfot.server.service;

import cn.edu.shu.pourrfot.server.model.InboxMessage;
import cn.edu.shu.pourrfot.server.model.dto.SingleMessage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author spencercjh
 */
public interface InboxMessageService extends IService<InboxMessage> {

  /**
   * find message by inbox message
   *
   * @param sender    sender user id
   * @param receiver  receiver user id
   * @param title     message title
   * @param isUrgent  message is urgent or not
   * @param isRegular message is regular or not
   * @param haveRead  inbox message have Read or not
   * @param current   current page
   * @param size      page size
   * @return page message
   */
  Page<SingleMessage> messagePage(Integer sender, Integer receiver, String title, Boolean isUrgent,
                                  Boolean isRegular, Boolean haveRead, int current, int size);

  /**
   * get message by inbox message
   *
   * @param id inbox message id
   * @return message but with inbox-message's createTime and updateTime
   */
  SingleMessage getMessageByInboxMessageId(Integer id);
}
