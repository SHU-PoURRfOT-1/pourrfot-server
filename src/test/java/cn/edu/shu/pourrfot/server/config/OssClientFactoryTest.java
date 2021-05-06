package cn.edu.shu.pourrfot.server.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = OssClientFactory.class)
@Slf4j
@ActiveProfiles("local")
@Disabled
@Ignore("Just for local test")
class OssClientFactoryTest {
  @Autowired
  private OSS ossClient;

  @Test
  void ossClient(@Value("${aliyun.oss.bucket}") String bucket) {
    final List<OSSObjectSummary> objectSummaries = ossClient.listObjectsV2(bucket)
      .getObjectSummaries();
    assertTrue(objectSummaries.size() > 0);
    objectSummaries.forEach(ossObjectSummary -> log.info(ossObjectSummary.getKey()));
  }
}
