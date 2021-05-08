package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.ResourceTypeEnum;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.model.OssFile;
import cn.edu.shu.pourrfot.server.repository.CourseGroupMapper;
import cn.edu.shu.pourrfot.server.repository.CourseMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMapper;
import cn.edu.shu.pourrfot.server.repository.StudentTransactionMapper;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
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
  private StudentTransactionMapper studentTransactionMapper;

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
    given(studentTransactionMapper.selectById(anyInt())).willReturn(null);
    assertThrows(NotFoundException.class, () -> ossFileService.save(OssFile.builder()
      .resourceId(999)
      .resourceType(ResourceTypeEnum.transactions)
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
    public StudentTransactionMapper studentTransactionMapper() {
      return Mockito.mock(StudentTransactionMapper.class);
    }
  }
}
