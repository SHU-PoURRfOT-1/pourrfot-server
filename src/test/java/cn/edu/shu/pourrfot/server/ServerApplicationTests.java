package cn.edu.shu.pourrfot.server;

import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;

@Slf4j
@SpringBootTest
class ServerApplicationTests {

  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();

  @Test
  void contextLoads() {
    log.info(customMySQLContainer.getDockerImageName());
    log.info(customMySQLContainer.getDatabaseName());
    log.info(customMySQLContainer.getUsername());
    log.info(customMySQLContainer.getPassword());
  }

}
