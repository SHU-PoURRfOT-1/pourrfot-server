package cn.edu.shu.pourrfot.server.service;

import cn.edu.shu.pourrfot.server.enums.ResourceTypeEnum;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.exception.OssFileServiceException;
import cn.edu.shu.pourrfot.server.model.OssFile;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.VoidResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
class OssServiceImplTest {
  private final OSSObject mockOssObject = new OSSObject();
  private final OssFile mockOssFile = OssFile.builder()
    .name("test.txt")
    .metadata(Map.of("test", "test"))
    .resourceType(ResourceTypeEnum.courses)
    .resourceId(100)
    .ownerId(100)
    .ossKey("mock")
    .ossUrl("mock")
    .originOssUrl("oss://pourrfot/caches/test.txt")
    .build();
  @Autowired
  private OssService ossService;
  private File file;
  @MockBean
  private OSS ossClient;

  @BeforeEach
  void setUp() throws FileNotFoundException {
    file = ResourceUtils.getFile("classpath:test-files/test.txt");
    mockOssObject.setObjectContent(new BufferedInputStream(new FileInputStream(file)));
  }

  @Test
  void getDirectoryFromOssUrl() {
    final String ossUrl = "oss://pourrfot/caches/test.txt";
    // normal
    assertEquals("/caches", OssService.getDirectoryFromOssUrl(ossUrl));
    // root
    assertEquals("/", OssService.getDirectoryFromOssUrl(OssService.OSS_SCHEME_PREFIX + "/bucket/test.txt"));
    assertThrows(IllegalArgumentException.class, () -> OssService.getDirectoryFromOssUrl("illegal"));
  }

  @Test
  void getKeyFromOssUrl() {
    final String ossUrl = "oss://pourrfot/caches/test.txt";
    assertEquals("caches/test.txt", OssService.getKeyFromOssUrl(ossUrl));
  }

  @Test
  void uploadFileWithKey() throws Throwable {
    when(ossClient.putObject(anyString(), anyString(), any(InputStream.class)))
      .thenReturn(new PutObjectResult());
    final String key = "caches/text.txt";
    assertEquals("oss://pourrfot/" + key, ossService.uploadFileWithKey(new MockMultipartFile(file.getName(),
      new BufferedInputStream(new FileInputStream(file))), key));
  }

  @Test
  void uploadFileWithKeyFailed() {
    given(ossClient.putObject(anyString(), anyString(), any(InputStream.class)))
      .willThrow(OSSException.class);
    assertThrows(OssFileServiceException.class, () -> ossService.uploadFileWithFilename(
      new MockMultipartFile(file.getName(), new BufferedInputStream(new FileInputStream(file))), file.getName()));
  }

  @Test
  void uploadFileWithFilename() throws Throwable {
    when(ossClient.putObject(anyString(), anyString(), any(InputStream.class)))
      .thenReturn(new PutObjectResult());
    final String key = "caches/text.txt";
    assertEquals("oss://pourrfot/" + key, ossService.uploadFileWithFilename(new MockMultipartFile(file.getName(),
      new BufferedInputStream(new FileInputStream(file))), "text.txt"));
  }

  @Test
  void getOssFileResource() {
    when(ossClient.getObject(anyString(), anyString())).thenReturn(mockOssObject);
    assertNotNull(ossService.getOssFileResource(mockOssFile));
  }

  @Test
  void getOssFileResourceFailed() {
    given(ossClient.getObject(anyString(), anyString())).willThrow(OSSException.class);
    assertThrows(OssFileServiceException.class, () -> ossService.getOssFileResource(mockOssFile));
  }

  @Test
  void checkOssObjectExisted() {
    given(ossClient.doesObjectExist(anyString(), anyString())).willReturn(true);
    assertTrue(ossService.checkOssObjectExisted("oss://pourrfot/caches/test.txt"));
  }

  @Test
  void checkOssObjectExistedFailed() {
    given(ossClient.doesObjectExist(anyString(), anyString())).willReturn(false);
    assertThrows(NotFoundException.class, () -> ossService.checkOssObjectExisted("oss://pourrfot/caches/test.txt"));

    given(ossClient.doesObjectExist(anyString(), anyString())).willThrow(OSSException.class);
    assertThrows(OssFileServiceException.class, () -> ossService.checkOssObjectExisted("oss://pourrfot/caches/test.txt"));
  }

  @Test
  void createSymbolLink() {
    given(ossClient.createSymlink(any())).willReturn(new VoidResult());
    assertEquals("oss://pourrfot/symbolLink", ossService.createSymbolLink("oss://pourrfot/caches/test.txt",
      "oss://pourrfot/symbolLink", Map.of("test", "test")));
  }

  @Test
  void createSymbolLinkFailed() {
    given(ossClient.createSymlink(any())).willThrow(OSSException.class);
    assertThrows(OssFileServiceException.class, () -> ossService.createSymbolLink(
      "oss://pourrfot/caches/test.txt", "oss://pourrfot/symbolLink", Collections.emptyMap()));
  }

  @Test
  void deleteOssObject() {
    given(ossClient.deleteObject(anyString(), anyString())).willReturn(new VoidResult());
    assertTrue(ossService.deleteOssObject("oss://pourrfot/caches/test.txt"));
  }

  @Test
  void deleteOssObjectFailed() {
    given(ossClient.deleteObject(anyString(), anyString())).willThrow(OSSException.class);
    assertThrows(OssFileServiceException.class, () -> ossService.deleteOssObject("oss://pourrfot/caches/test.txt"));
  }

  @Test
  void setupOssUrl() {
    assertEquals("oss://pourrfot/abc", ossService.setupOssUrl("abc"));
  }
}
