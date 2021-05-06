package cn.edu.shu.pourrfot.server.config;

import com.aliyun.oss.OSS;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class MockOssClientFactory {
  @Bean
  @Primary
  public OSS mockOss() {
    return Mockito.mock(OSS.class);
  }
}
