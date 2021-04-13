package cn.edu.shu.pourrfot.server;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ServerApplicationTests {

  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();

  @Test
  void contextLoads() {
    assertEquals("mysql:8.0", customMySQLContainer.getDockerImageName());
    assertEquals("test", customMySQLContainer.getDatabaseName());
    assertEquals("test", customMySQLContainer.getUsername());
    assertEquals("test", customMySQLContainer.getPassword());
  }

}
