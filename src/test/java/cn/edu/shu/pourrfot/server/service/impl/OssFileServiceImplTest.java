package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.ResourceTypeEnum;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.OssFile;
import cn.edu.shu.pourrfot.server.model.dto.CompleteOssFile;
import cn.edu.shu.pourrfot.server.repository.*;
import cn.edu.shu.pourrfot.server.service.OssFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Slf4j
class OssFileServiceImplTest {
  @Autowired
  private OssFileService ossFileService;
  @Autowired
  private CourseMapper courseMapper;
  @Autowired
  private CourseGroupMapper courseGroupMapper;
  @Autowired
  private ProjectMapper projectMapper;
  @Autowired
  private PourrfotTransactionMapper pourrfotTransactionMapper;
  @Autowired
  private MessageMapper messageMapper;
  @Autowired
  private OssFileMapper ossFileMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @Test
  void saveCourseFileFailed() {
    given(courseMapper.selectById(anyInt())).willReturn(null);
    assertThrows(NotFoundException.class, () -> ossFileService.save(OssFile.builder()
      .resourceId(999)
      .resourceType(ResourceTypeEnum.courses)
      .originOssUrl("mock")
      .build()));
  }

  @Test
  void saveCourseGroupFileFailed() {
    given(courseGroupMapper.selectById(anyInt())).willReturn(null);
    assertThrows(NotFoundException.class, () -> ossFileService.save(OssFile.builder()
      .resourceId(999)
      .resourceType(ResourceTypeEnum.groups)
      .originOssUrl("mock")
      .build()));
  }

  @Test
  void saveProjectFileFailed() {
    given(projectMapper.selectById(anyInt())).willReturn(null);
    assertThrows(NotFoundException.class, () -> ossFileService.save(OssFile.builder()
      .resourceId(999)
      .resourceType(ResourceTypeEnum.projects)
      .originOssUrl("mock")
      .build()));
  }

  @Test
  void saveStudentTransactionFileFailed() {
    given(pourrfotTransactionMapper.selectById(anyInt())).willReturn(null);
    assertThrows(NotFoundException.class, () -> ossFileService.save(OssFile.builder()
      .resourceId(999)
      .resourceType(ResourceTypeEnum.transactions)
      .originOssUrl("mock")
      .build()));
  }

  @Test
  void saveMessageFileFailed() {
    given(messageMapper.selectById(anyInt())).willReturn(null);
    assertThrows(NotFoundException.class, () -> ossFileService.save(OssFile.builder()
      .resourceId(999)
      .resourceType(ResourceTypeEnum.messages)
      .originOssUrl("mock")
      .build()));
  }

  @Test
  void saveUnknownFileFailed() {
    assertThrows(IllegalArgumentException.class, () -> ossFileService.save(OssFile.builder()
      .resourceId(999)
      .resourceType(ResourceTypeEnum.unknown)
      .originOssUrl("mock")
      .build()));
  }

  @Test
  void updateById() {
    assertThrows(NotImplementedException.class, () -> ossFileService.updateById(OssFile.builder()
      .id(999)
      .resourceId(999)
      .resourceType(ResourceTypeEnum.unknown)
      .originOssUrl("mock")
      .build()));
  }

  @Test
  void getCompleteOssFileByIdWithCourse() {
    final OssFile mock = OssFile.builder()
      .id(1)
      .ossUrl("mock")
      .originOssUrl("mock")
      .directory("mock")
      .name("mock")
      .ownerId(100)
      .ossKey("mock")
      .resourceType(ResourceTypeEnum.courses)
      .resourceId(101)
      .build();
    given(ossFileMapper.selectById(eq(mock.getId()))).willReturn(mock);
    final Map<String, Object> course = new HashMap<>();
    course.put("course", "course");
    given(courseMapper.selectMaps(any())).willReturn(List.of(course));
    final Map<String, Object> user = new HashMap<>();
    user.put("user", "user");
    given(pourrfotUserMapper.selectMaps(any())).willReturn(List.of(user));
    final CompleteOssFile result = ossFileService.getCompleteOssFileById(mock.getId());
    assertFalse(result.getOwner().isEmpty());
    assertFalse(result.getResource().isEmpty());
  }

  @Test
  void getCompleteOssFileByIdWithGroup() {
    final OssFile mock = OssFile.builder()
      .id(1)
      .ossUrl("mock")
      .originOssUrl("mock")
      .directory("mock")
      .name("mock")
      .ownerId(100)
      .ossKey("mock")
      .resourceType(ResourceTypeEnum.groups)
      .resourceId(101)
      .build();
    given(ossFileMapper.selectById(eq(mock.getId()))).willReturn(mock);
    final Map<String, Object> group = new HashMap<>();
    group.put("group", "group");
    given(courseGroupMapper.selectMaps(any())).willReturn(List.of(group));
    final Map<String, Object> user = new HashMap<>();
    user.put("user", "user");
    given(pourrfotUserMapper.selectMaps(any())).willReturn(List.of(user));
    final CompleteOssFile result = ossFileService.getCompleteOssFileById(mock.getId());
    assertFalse(result.getOwner().isEmpty());
    assertFalse(result.getResource().isEmpty());
  }

  @Test
  void getCompleteOssFileByIdWithProject() {
    final OssFile mock = OssFile.builder()
      .id(1)
      .ossUrl("mock")
      .originOssUrl("mock")
      .directory("mock")
      .name("mock")
      .ownerId(100)
      .ossKey("mock")
      .resourceType(ResourceTypeEnum.projects)
      .resourceId(101)
      .build();
    given(ossFileMapper.selectById(eq(mock.getId()))).willReturn(mock);
    final Map<String, Object> project = new HashMap<>();
    project.put("project", "project");
    given(projectMapper.selectMaps(any())).willReturn(List.of(project));
    final Map<String, Object> user = new HashMap<>();
    user.put("user", "user");
    given(pourrfotUserMapper.selectMaps(any())).willReturn(List.of(user));
    final CompleteOssFile result = ossFileService.getCompleteOssFileById(mock.getId());
    assertFalse(result.getOwner().isEmpty());
    assertFalse(result.getResource().isEmpty());
  }

  @Test
  void getCompleteOssFileByIdWithTransaction() {
    final OssFile mock = OssFile.builder()
      .id(1)
      .ossUrl("mock")
      .originOssUrl("mock")
      .directory("mock")
      .name("mock")
      .ownerId(100)
      .ossKey("mock")
      .resourceType(ResourceTypeEnum.transactions)
      .resourceId(101)
      .build();
    given(ossFileMapper.selectById(eq(mock.getId()))).willReturn(mock);
    final Map<String, Object> transaction = new HashMap<>();
    transaction.put("transaction", "transaction");
    given(pourrfotTransactionMapper.selectMaps(any())).willReturn(List.of(transaction));
    final Map<String, Object> user = new HashMap<>();
    user.put("user", "user");
    given(pourrfotUserMapper.selectMaps(any())).willReturn(List.of(user));
    final CompleteOssFile result = ossFileService.getCompleteOssFileById(mock.getId());
    assertFalse(result.getOwner().isEmpty());
    assertFalse(result.getResource().isEmpty());
  }

  @Test
  void getCompleteOssFileByIdWithMessage() {
    final OssFile mock = OssFile.builder()
      .id(1)
      .ossUrl("mock")
      .originOssUrl("mock")
      .directory("mock")
      .name("mock")
      .ownerId(100)
      .ossKey("mock")
      .resourceType(ResourceTypeEnum.messages)
      .resourceId(101)
      .build();
    given(ossFileMapper.selectById(eq(mock.getId()))).willReturn(mock);
    final Map<String, Object> message = new HashMap<>();
    message.put("message", "message");
    given(messageMapper.selectMaps(any())).willReturn(List.of(message));
    final Map<String, Object> user = new HashMap<>();
    user.put("user", "user");
    given(pourrfotUserMapper.selectMaps(any())).willReturn(List.of(user));
    final CompleteOssFile result = ossFileService.getCompleteOssFileById(mock.getId());
    assertFalse(result.getOwner().isEmpty());
    assertFalse(result.getResource().isEmpty());
  }

  @Test
  void getCompleteOssFileByIdWithUnknown() {
    final OssFile mock = OssFile.builder()
      .id(1)
      .ossUrl("mock")
      .originOssUrl("mock")
      .directory("mock")
      .name("mock")
      .ossKey("mock")
      .resourceType(ResourceTypeEnum.unknown)
      .resourceId(101)
      .build();
    given(ossFileMapper.selectById(eq(mock.getId()))).willReturn(mock);
    assertThrows(IllegalArgumentException.class, () -> ossFileService.getCompleteOssFileById(mock.getId()),
      "Not Support oss-file resource type");
  }

  @Test
  void getCompleteOssFileByIdWithoutResource() {
    final OssFile mock = OssFile.builder()
      .id(1)
      .ossUrl("mock")
      .originOssUrl("mock")
      .directory("mock")
      .name("mock")
      .ossKey("mock")
      .build();
    given(ossFileMapper.selectById(eq(mock.getId()))).willReturn(mock);
    assertNotNull(ossFileService.getCompleteOssFileById(mock.getId()));
  }

  @TestConfiguration
  public static class TestConfig {
    @Bean
    @Primary
    public CourseMapper courseMapper() {
      return Mockito.mock(CourseMapper.class);
    }

    @Bean
    @Primary
    public CourseGroupMapper courseGroupMapper() {
      return Mockito.mock(CourseGroupMapper.class);
    }

    @Bean
    @Primary
    public ProjectMapper projectMapper() {
      return Mockito.mock(ProjectMapper.class);
    }

    @Bean
    @Primary
    public PourrfotTransactionMapper studentTransactionMapper() {
      return Mockito.mock(PourrfotTransactionMapper.class);
    }

    @Bean
    @Primary
    public MessageMapper messageMapper() {
      return Mockito.mock(MessageMapper.class);
    }

    @Bean
    @Primary
    public PourrfotUserMapper pourrfotUserMapper() {
      return Mockito.mock(PourrfotUserMapper.class);
    }

    @Bean
    @Primary
    public OssFileMapper ossFileMapper() {
      return Mockito.mock(OssFileMapper.class);
    }
  }
}
