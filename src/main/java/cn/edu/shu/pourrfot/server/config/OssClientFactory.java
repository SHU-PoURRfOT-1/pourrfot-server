package cn.edu.shu.pourrfot.server.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author spencercjh
 */
@Slf4j
@Configuration
public class OssClientFactory {
  @Bean
  public OSS ossClient(@Value("${aliyun.accessKeyId:}") String accessKeyId,
                       @Value("${aliyun.accessKeySecret:}") String accessKeySecret,
                       @Value("${aliyun.oss.endpoint:}") String endpoint,
                       @Value("${aliyun.oss.bucket:pourrfot}") String bucket) {
    if (StringUtils.isBlank(accessKeyId) || StringUtils.isBlank(accessKeySecret) || StringUtils.isBlank(endpoint)
      || StringUtils.isBlank(bucket)) {
      log.warn("Illegal OSS config, can't inject OSS");
      return null;
    }
    return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
  }
}
