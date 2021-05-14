package cn.edu.shu.pourrfot.server.repository;

import cn.edu.shu.pourrfot.server.model.InboxMessage;
import cn.edu.shu.pourrfot.server.model.dto.SingleMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author spencercjh
 */
@Mapper
public interface InboxMessageMapper extends BaseMapper<InboxMessage> {
  /**
   * get a message joining inbox-message
   *
   * @param id inbox message id
   * @return message but with inbox-message's createTime and updateTime
   */
  SingleMessage selectMessageByInboxMessageId(@Param("id") Integer id);

  /**
   * search messages joining inbox-message with conditions
   *
   * @param startOffset current page in limit
   * @param rowCount    page size in limit
   * @param sender      sender id in where with eq
   * @param receiver    receiver in where with eq
   * @param title       title in where with like
   * @param isUrgent    urgent in where with eq
   * @param isRegular   regular in where with eq
   * @param haveRead    have_read in where with eq
   * @return messages but with inbox-message's createTime and updateTime
   */
  List<SingleMessage> selectMessagesByCondition(@Param("startOffset") long startOffset,
                                                @Param("rowCount") long rowCount,
                                                @Param("sender") Integer sender,
                                                @Param("receiver") Integer receiver,
                                                @Param("title") String title,
                                                @Param("isUrgent") Boolean isUrgent,
                                                @Param("isRegular") Boolean isRegular,
                                                @Param("haveRead") Boolean haveRead);

  /**
   * count total of {@link #selectMessagesByCondition(long, long, Integer, Integer, String, Boolean, Boolean, Boolean)}
   *
   * @param sender    sender id in where with eq
   * @param receiver  receiver in where with eq
   * @param title     title in where with like
   * @param isUrgent  urgent in where with eq
   * @param isRegular regular in where with eq
   * @param haveRead  have_read in where with eq
   * @return total
   */
  Integer countMessagesByCondition(@Param("sender") Integer sender,
                                   @Param("receiver") Integer receiver,
                                   @Param("title") String title,
                                   @Param("isUrgent") Boolean isUrgent,
                                   @Param("isRegular") Boolean isRegular,
                                   @Param("haveRead") Boolean haveRead);
}
