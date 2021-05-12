package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.model.Message;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.dto.SingleMessage;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.InboxMessageService;
import cn.edu.shu.pourrfot.server.service.MessageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Slf4j
class InboxMessageServiceImplTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  final private PourrfotUser sender = PourrfotUser.builder()
    .id(100)
    .username("123456")
    .nickname("mock-user1")
    .sex(SexEnum.male)
    .role(RoleEnum.student)
    .password("123456")
    .build();
  final private PourrfotUser receiver = PourrfotUser.builder()
    .id(200)
    .username("456789")
    .nickname("mock-user2")
    .sex(SexEnum.male)
    .role(RoleEnum.student)
    .password("456789")
    .build();
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private MessageService messageService;
  @Autowired
  private InboxMessageService inboxMessageService;

  @BeforeEach
  void setUp() {
    given(pourrfotUserMapper.selectById(eq(100))).willReturn(sender);
    given(pourrfotUserMapper.selectById(eq(200))).willReturn(receiver);
  }

  @Transactional
  @Test
  void messagePage() {
    final Message toCreate = Message.builder()
      .sender(sender.getId())
      .receiver(receiver.getId())
      .title("mock title ")
      .content("mock content ")
      .urgent(false)
      .regular(true)
      .build();
    final int total = 50;
    for (int i = 0; i < total; i++) {
      messageService.save(toCreate
        .setTitle(toCreate.getTitle() + i)
        .setContent(toCreate.getContent() + i)
        .setUrgent(i % 2 == 0)
        .setRegular(i % 3 == 0));
    }
    Page<SingleMessage> pageResult = inboxMessageService.messagePage(sender.getId(), null,
      null, null, null, null, 1, 10);
    assertEquals(1, pageResult.getCurrent());
    assertEquals(10, pageResult.getSize());
    assertEquals(total, pageResult.getTotal());
    pageResult = inboxMessageService.messagePage(sender.getId(), receiver.getId(),
      null, null, null, null, 1, 10);
    assertEquals(1, pageResult.getCurrent());
    assertEquals(10, pageResult.getSize());
    assertEquals(total, pageResult.getTotal());
    pageResult = inboxMessageService.messagePage(null, receiver.getId(),
      null, null, null, null, 1, 10);
    assertEquals(1, pageResult.getCurrent());
    assertEquals(10, pageResult.getSize());
    assertEquals(total, pageResult.getTotal());
    pageResult = inboxMessageService.messagePage(null, null,
      "title", null, null, null, 1, 10);
    assertEquals(1, pageResult.getCurrent());
    assertEquals(10, pageResult.getSize());
    assertEquals(total, pageResult.getTotal());
    pageResult = inboxMessageService.messagePage(null, null,
      null, true, null, null, 1, 10);
    assertEquals(1, pageResult.getCurrent());
    assertEquals(10, pageResult.getSize());
    assertEquals(total / 2, pageResult.getTotal());
    pageResult = inboxMessageService.messagePage(null, null,
      null, null, true, null, 1, 10);
    assertEquals(1, pageResult.getCurrent());
    assertEquals(10, pageResult.getSize());
    assertEquals(total / 3 + 1, pageResult.getTotal());
  }

  @TestConfiguration
  public static class TestConfig {
    @Bean
    @Primary
    public PourrfotUserMapper pourrfotUserMapper() {
      return Mockito.mock(PourrfotUserMapper.class);
    }
  }
}
