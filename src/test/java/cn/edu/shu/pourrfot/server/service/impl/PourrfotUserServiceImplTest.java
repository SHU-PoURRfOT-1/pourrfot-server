package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.service.PourrfotUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
class PourrfotUserServiceImplTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  private final PourrfotUser user = PourrfotUser.builder()
    .username("teacher11")
    .nickname("teacher11")
    .password("password")
    .role(RoleEnum.teacher)
    .sex(SexEnum.male)
    .build();
  @Autowired
  private PourrfotUserService pourrfotUserService;

  @Transactional
  @Test
  void updateById() {
    assertThrows(NotFoundException.class, () -> pourrfotUserService.updateById(user));
    assertTrue(pourrfotUserService.save(user));
    assertTrue(pourrfotUserService.updateById(user.setNickname("UPDATE")));
    assertThrows(IllegalCRUDOperationException.class,
      () -> pourrfotUserService.updateById(user.setRole(RoleEnum.admin)));
  }
}
