package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.StudentTransaction;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.repository.StudentTransactionMapper;
import cn.edu.shu.pourrfot.server.service.StudentTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Slf4j
class StudentTransactionServiceImplTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();

  @Autowired
  private StudentTransactionService studentTransactionService;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private StudentTransactionMapper studentTransactionMapper;

  @Test
  void saveFailedBecauseSamePeople() {
    assertThrows(IllegalCRUDOperationException.class, () -> studentTransactionService.save(StudentTransaction.builder()
      .sender(100)
      .receiver(100)
      .build()));
  }

  @Test
  void saveFailedBecauseSenderNotExist() {
    given(pourrfotUserMapper.selectById(eq(100))).willReturn(null);
    assertThrows(NotFoundException.class, () -> studentTransactionService.save(StudentTransaction.builder()
      .sender(100)
      .receiver(200)
      .build()));
  }

  @Test
  void saveFailedBecauseReceiverNotExist() {
    given(pourrfotUserMapper.selectById(eq(100))).willReturn(PourrfotUser.builder().id(100).build());
    given(pourrfotUserMapper.selectById(eq(200))).willReturn(null);
    assertThrows(NotFoundException.class, () -> studentTransactionService.save(StudentTransaction.builder()
      .sender(100)
      .receiver(200)
      .build()));
  }

  @Test
  void updateByIdFailedBecauseNotExist() {
    given(studentTransactionMapper.selectById(eq(999))).willReturn(null);
    assertThrows(NotFoundException.class, () -> studentTransactionService.updateById(StudentTransaction.builder()
      .id(999)
      .sender(999)
      .receiver(999)
      .build()));
  }

  @Test
  void updateByIdFailedBecauseIllegalOperation() {
    final StudentTransaction mock = StudentTransaction.builder()
      .id(999)
      .sender(100)
      .receiver(200)
      .build();
    given(studentTransactionMapper.selectById(eq(999))).willReturn(mock);
    given(pourrfotUserMapper.selectById(eq(100))).willReturn(PourrfotUser.builder().id(100).build());
    given(pourrfotUserMapper.selectById(eq(200))).willReturn(PourrfotUser.builder().id(200).build());
    given(pourrfotUserMapper.selectById(eq(300))).willReturn(PourrfotUser.builder().id(300).build());
    assertThrows(IllegalCRUDOperationException.class, () -> studentTransactionService.updateById(StudentTransaction.builder()
      .id(999)
      .sender(300)
      .receiver(200)
      .build()));
  }

  @TestConfiguration
  public static class TestConfig {

    @Bean
    @Primary
    public PourrfotUserMapper pourrfotUserMapper() {
      return Mockito.mock(PourrfotUserMapper.class);
    }

    @Bean
    @Primary
    public StudentTransactionMapper studentTransactionMapper() {
      return Mockito.spy(StudentTransactionMapper.class);
    }
  }
}
