package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.service.StudentTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;

import static cn.edu.shu.pourrfot.server.model.StudentTransaction.builder;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
class StudentTransactionServiceImplTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();

  @Autowired
  private StudentTransactionService studentTransactionService;

  @Test
  void saveFailed() {
    assertThrows(NotFoundException.class, () -> studentTransactionService.save(builder()
      .sender(999)
      .receiver(999)
      .build()));
  }

  @Test
  void updateByIdFailed() {
    assertThrows(NotFoundException.class, () -> studentTransactionService.updateById(builder()
      .id(999)
      .sender(999)
      .receiver(999)
      .build()));
  }
}
