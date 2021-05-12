package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.model.InboxMessage;
import cn.edu.shu.pourrfot.server.repository.InboxMessageMapper;
import cn.edu.shu.pourrfot.server.service.InboxMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author spencercjh
 */
@Service
public class InboxMessageServiceImpl extends ServiceImpl<InboxMessageMapper, InboxMessage> implements InboxMessageService {

}
