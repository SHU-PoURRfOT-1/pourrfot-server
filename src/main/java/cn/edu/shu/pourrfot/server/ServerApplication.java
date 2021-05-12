package cn.edu.shu.pourrfot.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author spencercjh
 */
@SpringBootApplication
@MapperScan("cn.edu.shu.pourrfot.server.repository")
public class ServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }
}
