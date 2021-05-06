package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.service.OssFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;
import org.testcontainers.containers.MySQLContainer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@ActiveProfiles("local")
@Disabled
@Ignore
class LocalOssFileServiceImplTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  @Autowired
  private OssFileService ossFileService;

  @Test
  void localTest() throws Throwable {
    final File testFile = ResourceUtils.getFile("classpath:test-files/test.txt");
    final String ossUrl = ossFileService.uploadFileWithFilename(new MockMultipartFile(testFile.getName(),
      new BufferedInputStream(new FileInputStream(testFile))), testFile.getName());
    assertTrue(StringUtils.isNotBlank(ossUrl));
    log.info(ossUrl);
  }
}
