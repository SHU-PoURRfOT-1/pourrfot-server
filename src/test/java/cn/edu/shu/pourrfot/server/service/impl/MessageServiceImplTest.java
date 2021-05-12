package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.Message;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Slf4j
class MessageServiceImplTest {
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
  private MessageService messageService;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @Transactional
  @Test
  void save() {
    given(pourrfotUserMapper.selectById(eq(100))).willReturn(sender);
    given(pourrfotUserMapper.selectById(eq(200))).willReturn(receiver);
    final Message toCreate = Message.builder()
      .sender(sender.getId())
      .receiver(receiver.getId())
      .title("mock title1")
      .content("mock content1")
      .urgent(false)
      .regular(true)
      .build();
    assertTrue(messageService.save(toCreate));
  }

  @Transactional
  @Test
  void saveFailed1() {
    given(pourrfotUserMapper.selectById(eq(100))).willReturn(null);
    final Message toCreate = Message.builder()
      .sender(sender.getId())
      .receiver(receiver.getId())
      .title("mock title1")
      .content("mock content1")
      .urgent(false)
      .regular(true)
      .build();
    assertThrows(NotFoundException.class, () -> messageService.save(toCreate));
  }

  @Transactional
  @Test
  void saveFailed2() {
    given(pourrfotUserMapper.selectById(eq(100))).willReturn(sender);
    given(pourrfotUserMapper.selectById(eq(200))).willReturn(null);
    final Message toCreate = Message.builder()
      .sender(sender.getId())
      .receiver(receiver.getId())
      .title("mock title1")
      .content("mock content1")
      .urgent(false)
      .regular(true)
      .build();
    assertThrows(NotFoundException.class, () -> messageService.save(toCreate));
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
