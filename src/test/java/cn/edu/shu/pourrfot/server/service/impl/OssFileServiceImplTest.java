package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.ResourceTypeEnum;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.exception.OssFileServiceException;
import cn.edu.shu.pourrfot.server.model.OssFile;
import cn.edu.shu.pourrfot.server.service.OssFileService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

/**
 * TODO: make pass in testcontainer env
 */
@Slf4j
@SpringBootTest
@Ignore
@Disabled
@ActiveProfiles("local")
class OssFileServiceImplTest {

  @MockBean
  private OSS ossClient;
  @Autowired
  private OssFileService ossFileService;
  private File file;

  @BeforeEach
  void setUp() throws FileNotFoundException {
    file = ResourceUtils.getFile("classpath:test-files/test.txt");
  }

  @Test
  void getDirectoryFromOssUrl() {
    final String ossUrl = "oss://pourrfot/caches/test.txt";
    // normal
    assertEquals("/caches", OssFileService.getDirectoryFromOssUrl(ossUrl));
    // root
    assertEquals("/", OssFileService.getDirectoryFromOssUrl(OssFileService.OSS_SCHEME_PREFIX + "/bucket/test.txt"));
    assertThrows(IllegalArgumentException.class, () -> OssFileService.getDirectoryFromOssUrl("illegal"));
  }

  @Test
  void getKeyFromOssUrl() {
    final String ossUrl = "oss://pourrfot/caches/test.txt";
    assertEquals("caches/test.txt", OssFileService.getKeyFromOssUrl(ossUrl));
  }

  @Test
  void uploadFileWithKeySuccess() throws Throwable {
    final String key = "caches/text.txt";
    assertEquals("oss://pourrfot/" + key, ossFileService.uploadFileWithKey(new MockMultipartFile(file.getName(),
      new BufferedInputStream(new FileInputStream(file))), key));
  }

  @Test
  void uploadFileWithKeyFailed() {
    given(ossClient.putObject(anyString(), anyString(), any(InputStream.class)))
      .willThrow(OSSException.class);
    assertThrows(OssFileServiceException.class, () -> ossFileService.uploadFileWithFilename(
      new MockMultipartFile(file.getName(), new BufferedInputStream(new FileInputStream(file))), file.getName()));
  }

  @Test
  void save() {
    when(ossClient.doesObjectExist(anyString(), anyString())).thenReturn(true);
    final OssFile mock = OssFile.builder()
      .name("test.txt")
      .metadata(Map.of("test", "test"))
      .resourceType(ResourceTypeEnum.courses)
      .resourceId(100)
      .ownerId(100)
      .originOssUrl("oss://pourrfot/caches/test.txt")
      .build();
    assertTrue(ossFileService.save(mock));
    assertEquals("courses/100/test.txt", mock.getOssKey());
    assertEquals("/courses/100", mock.getDirectory());
    assertEquals("oss://pourrfot/courses/100/test.txt", mock.getOssUrl());
    assertEquals("oss://pourrfot/caches/test.txt", mock.getOriginOssUrl());
    assertEquals("test", mock.getMetadata().get("test"));
  }

  @Test
  void failedSaveBecauseFileNotExisted() {
    when(ossClient.doesObjectExist(anyString(), anyString())).thenReturn(false);
    final OssFile mock = OssFile.builder()
      .name("test.txt")
      .metadata(Map.of("test", "test"))
      .resourceType(ResourceTypeEnum.courses)
      .resourceId(100)
      .ownerId(100)
      .originOssUrl("oss://pourrfot/caches/test.txt")
      .build();
    assertThrows(NotFoundException.class, () -> ossFileService.save(mock));
  }

  @Test
  void failedSaveBecauseOssExceptionDuringChecking() {
    when(ossClient.doesObjectExist(anyString(), anyString())).thenThrow(new OSSException("mock"));
    final OssFile mock = OssFile.builder()
      .name("test.txt")
      .metadata(Map.of("test", "test"))
      .resourceType(ResourceTypeEnum.courses)
      .resourceId(100)
      .ownerId(100)
      .originOssUrl("oss://pourrfot/caches/test.txt")
      .build();
    assertThrows(OssFileServiceException.class, () -> ossFileService.save(mock));
  }

  @Test
  void failedSaveBecauseOssExceptionDuringSaving() {
    when(ossClient.doesObjectExist(anyString(), anyString())).thenReturn(true);
    when(ossClient.createSymlink(any())).thenThrow(new OSSException("mock"));
    final OssFile mock = OssFile.builder()
      .name("test.txt")
      .metadata(Map.of("test", "test"))
      .resourceType(ResourceTypeEnum.courses)
      .resourceId(100)
      .ownerId(100)
      .originOssUrl("oss://pourrfot/caches/test.txt")
      .build();
    assertThrows(OssFileServiceException.class, () -> ossFileService.save(mock));
  }

  @Test
  void removeById() {
    when(ossClient.doesObjectExist(anyString(), anyString())).thenReturn(true);
    final OssFile mock = OssFile.builder()
      .name("test.txt")
      .metadata(Map.of("test", "test"))
      .resourceType(ResourceTypeEnum.courses)
      .resourceId(100)
      .ownerId(100)
      .originOssUrl("oss://pourrfot/caches/test.txt")
      .build();
    assertTrue(ossFileService.save(mock));
    assertTrue(ossFileService.removeById(mock.getId()));
  }

  @Test
  void removeByIdFailedBecauseNotExist() {
    assertFalse(ossFileService.removeById(999));
  }

  @Test
  void removeByIdFailedBecauseOssExceptionDuringDeleting() {
    when(ossClient.doesObjectExist(anyString(), anyString())).thenReturn(true);
    final OssFile mock = OssFile.builder()
      .name("test.txt")
      .metadata(Map.of("test", "test"))
      .resourceType(ResourceTypeEnum.courses)
      .resourceId(100)
      .ownerId(100)
      .originOssUrl("oss://pourrfot/caches/test.txt")
      .build();
    assertTrue(ossFileService.save(mock));
    when(ossClient.deleteObject(anyString(), anyString())).thenThrow(new OSSException("mock"));
    assertThrows(OssFileServiceException.class, () -> ossFileService.removeById(mock.getId()));
  }

  @Test
  void getOssFileResource() throws FileNotFoundException {
    final OSSObject mockResult = new OSSObject();
    mockResult.setObjectContent(new BufferedInputStream(new FileInputStream(file)));
    when(ossClient.getObject(anyString(), anyString())).thenReturn(mockResult);
    final OssFile mock = OssFile.builder()
      .name("test.txt")
      .metadata(Map.of("test", "test"))
      .resourceType(ResourceTypeEnum.courses)
      .resourceId(100)
      .ownerId(100)
      .ossKey("mock")
      .ossUrl("mock")
      .originOssUrl("oss://pourrfot/caches/test.txt")
      .build();
    assertNotNull(ossFileService.getOssFileResource(mock));
  }
}
