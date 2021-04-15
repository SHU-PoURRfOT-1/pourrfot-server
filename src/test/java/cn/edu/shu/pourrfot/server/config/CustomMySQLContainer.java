package cn.edu.shu.pourrfot.server.config;

import org.testcontainers.containers.MySQLContainer;

/**
 * reference: https://www.baeldung.com/spring-boot-testcontainers-integration-test
 * Make the database in the docker image running with test classes single
 *
 * @author spencercjh
 */
public class CustomMySQLContainer extends MySQLContainer<CustomMySQLContainer> {
  private static final String IMAGE_VERSION = "mysql:8.0";
  private static CustomMySQLContainer container;

  private CustomMySQLContainer() {
    super(IMAGE_VERSION);
  }

  public static CustomMySQLContainer getInstance() {
    if (container == null) {
      container = new CustomMySQLContainer();
    }
    return container;
  }

  @Override
  public void stop() {
  }
}
