package cn.edu.shu.pourrfot.server.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author spencercjh
 */
@Configuration
public class OssClientFactory {
  @Bean
  public OSS ossClient(@Value("${aliyun.accessKeyId:}") String accessKeyId,
                       @Value("${aliyun.accessKeySecret:}") String accessKeySecret,
                       @Value("${aliyun.oss.endpoint:}") String endpoint) {
    if (StringUtils.isBlank(accessKeyId) || StringUtils.isBlank(accessKeySecret) || StringUtils.isBlank(endpoint)) {
      return null;
    }
    return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
  }
}
