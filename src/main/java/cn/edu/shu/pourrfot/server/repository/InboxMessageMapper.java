package cn.edu.shu.pourrfot.server.repository;

import cn.edu.shu.pourrfot.server.model.InboxMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author spencercjh
 */
@Mapper
public interface InboxMessageMapper extends BaseMapper<InboxMessage> {
}
