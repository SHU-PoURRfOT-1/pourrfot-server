package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.ResourceTypeEnum;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.exception.OssFileServiceException;
import cn.edu.shu.pourrfot.server.model.OssFile;
import cn.edu.shu.pourrfot.server.repository.*;
import cn.edu.shu.pourrfot.server.service.OssFileService;
import cn.edu.shu.pourrfot.server.service.OssService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

/**
 * @author spencercjh
 */
@Slf4j
@Service
public class OssFileServiceImpl extends ServiceImpl<OssFileMapper, OssFile> implements OssFileService {
  @Autowired
  private OssService ossService;
  @Autowired
  private CourseMapper courseMapper;
  @Autowired
  private CourseGroupMapper courseGroupMapper;
  @Autowired
  private ProjectMapper projectMapper;
  @Autowired
  private StudentTransactionMapper studentTransactionMapper;

  /**
   * processes
   * <p>
   * <p>1. check OssFile#resourceType/resourceId in DB </p>
   * <p>2. check the cache file defined by oss url exist in OSS</p>
   * <p>3. create a symbol link of the cache to the corresponding location (maybe async) in OSS</p>
   * <p>4. save OssFile with new OSS info</p>
   * <p>throws {@link OssFileServiceException} any exception during oss operations</p>
   *
   * @param entity to save
   * @return success
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean save(OssFile entity) {
    // 1. check OssFile#resourceType/resourceId in DB
    checkAssociatedResourceExisted(entity.getName(), entity.getResourceId(), entity.getResourceType());
    // 2. check the cache file defined by oss url exist in OSS
    final String key = StringUtils.isNotBlank(entity.getOriginOssUrl()) ?
      OssService.getKeyFromOssUrl(entity.getOriginOssUrl()) : entity.getOssKey();
    ossService.checkOssObjectExisted(key);
    // 3. create a symbol link
    final String symbolLink = ossService.createSymbolLink(key, setupSymbolLink(entity), entity.getMetadata());
    // 4. save OssFile with new OSS info
    return baseMapper.insert(entity
      .setOssKey(symbolLink)
      .setDirectory(setupNewDirectory(entity.getResourceType(), entity.getResourceId()))
      .setOssUrl(ossService.setupOssUrl(symbolLink))
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
  }

  private void checkAssociatedResourceExisted(String name, int resourceId, ResourceTypeEnum resourceType) {
    switch (resourceType) {
      case courses:
        if (courseMapper.selectById(resourceId) == null) {
          final String message = String.format("Not found the course: %s associated with the oss-file: %s",
            resourceId, name);
          log.error(message);
          throw new NotFoundException(message);
        }
        break;
      case groups:
        if (courseGroupMapper.selectById(resourceId) == null) {
          final String message = String.format("Not found the course-group: %s associated with the oss-file: %s",
            resourceId, name);
          log.error(message);
          throw new NotFoundException(message);
        }
        break;
      case projects:
        if (projectMapper.selectById(resourceId) == null) {
          final String message = String.format("Not found the project: %s associated with the oss-file: %s",
            resourceId, name);
          log.error(message);
          throw new NotFoundException(message);
        }
        break;
      case transactions:
        if (studentTransactionMapper.selectById(resourceId) == null) {
          final String message = String.format("Not found the student-transaction: %s associated with the oss-file: %s",
            resourceId, name);
          log.error(message);
          throw new NotFoundException(message);
        }
        break;
      default:
        // TODO: check messages
        throw new IllegalArgumentException("Not Support oss-file resource type");
    }
  }

  @Override
  public boolean updateById(OssFile entity) {
    // NOT SUPPORT UPDATE
    throw new NotImplementedException();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean removeById(Serializable id) {
    final OssFile found = baseMapper.selectById(id);
    if (found == null) {
      return false;
    }
    // 1. delete the symbol link
    ossService.deleteOssObject(found.getOssKey());
    // 2. delete the origin file
    ossService.deleteOssObject(OssService.getKeyFromOssUrl(found.getOriginOssUrl()));
    return baseMapper.deleteById(id) == 1;
  }

  private String setupNewDirectory(ResourceTypeEnum resourceType, int resourceId) {
    return String.format("/%s/%d", resourceType.getValue(), resourceId);
  }

  private String setupSymbolLink(OssFile entity) {
    return setupSymbolLink(entity.getResourceType(), entity.getResourceId(), entity.getName());
  }

  private String setupSymbolLink(ResourceTypeEnum resourceType, int resourceId, String name) {
    return String.format("%s/%d/%s", resourceType.getValue(), resourceId, name);
  }
}
