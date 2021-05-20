package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.enums.ResourceTypeEnum;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.exception.OssFileServiceException;
import cn.edu.shu.pourrfot.server.model.*;
import cn.edu.shu.pourrfot.server.model.dto.CompleteOssFile;
import cn.edu.shu.pourrfot.server.model.dto.SimpleUser;
import cn.edu.shu.pourrfot.server.repository.*;
import cn.edu.shu.pourrfot.server.service.OssFileService;
import cn.edu.shu.pourrfot.server.service.OssService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  private PourrfotTransactionMapper pourrfotTransactionMapper;
  @Autowired
  private MessageMapper messageMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @Override
  public Page<CompleteOssFile> page(Page<OssFile> page, Wrapper<OssFile> queryWrapper) {
    final Page<OssFile> result = super.page(page, queryWrapper);
    return new Page<CompleteOssFile>(result.getCurrent(), result.getSize(), result.getTotal())
      .setRecords(result.getRecords()
        .stream()
        .map(ossFile -> CompleteOssFile.of(ossFile, getOssFileAssociatedUser(ossFile.getOwnerId()),
          getOssFileAssociatedResource(ossFile.getResourceType(), ossFile.getResourceId())))
        .collect(Collectors.toList()));
  }

  @Override
  public CompleteOssFile getCompleteOssFileById(int id) {
    final OssFile found = baseMapper.selectById(id);
    if (found == null) {
      return null;
    }
    return CompleteOssFile.of(found, getOssFileAssociatedUser(found.getOwnerId()),
      getOssFileAssociatedResource(found.getResourceType(), found.getResourceId()));
  }

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
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user != null) {
      entity.setOwnerId(user.getId());
    }
    // 1. check OssFile#resourceType/resourceId in DB
    checkAssociatedResourceExisted(entity.getName(), entity.getResourceId(), entity.getResourceType());
    // 2. check the cache file defined by oss url exist in OSS
    final String key = StringUtils.isNotBlank(entity.getOriginOssUrl()) ?
      OssService.getKeyFromOssUrl(entity.getOriginOssUrl()) : entity.getOssKey();
    ossService.checkOssObjectExisted(key);
    // 3. create a symbol link
    final String symbolLink = ossService.createSymbolLink(key, setupSymbolLink(entity),
      Collections.emptyMap());
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
        if (pourrfotTransactionMapper.selectById(resourceId) == null) {
          final String message = String.format("Not found the student-transaction: %s associated with the oss-file: %s",
            resourceId, name);
          log.error(message);
          throw new NotFoundException(message);
        }
        break;
      case messages:
        if (messageMapper.selectById(resourceId) == null) {
          final String message = String.format("Not found the message: %s associated with the oss-file: %s",
            resourceId, name);
          log.error(message);
          throw new NotFoundException(message);
        }
        break;
      default:
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

  private Map<String, Object> getOssFileAssociatedUser(Integer ownerId) {
    if (ownerId == null) {
      return Collections.emptyMap();
    }
    final List<Map<String, Object>> users = pourrfotUserMapper.selectMaps(new QueryWrapper<>(new PourrfotUser())
      .eq(PourrfotUser.COL_ID, ownerId));
    return CollectionUtils.isNotEmpty(users) ? users.get(0) : null;
  }

  private Map<String, Object> getOssFileAssociatedResource(ResourceTypeEnum resourceType, Integer resourceId) {
    Map<String, Object> resource = Collections.emptyMap();
    if (resourceType == null || resourceId == null) {
      return resource;
    }
    switch (resourceType) {
      case courses:
        final List<Map<String, Object>> courseResult = courseMapper.selectMaps(new QueryWrapper<>(new Course())
          .eq(Course.COL_ID, resourceId));
        if (CollectionUtils.isNotEmpty(courseResult)) {
          resource = courseResult.get(0);
          resource.put("title", "课程：" + resource.get("course_name"));
        }
        break;
      case groups:
        final List<Map<String, Object>> groupResult = courseGroupMapper.selectMaps(new QueryWrapper<>(new CourseGroup())
          .eq(CourseGroup.COL_ID, resourceId));
        if (CollectionUtils.isNotEmpty(groupResult)) {
          resource = groupResult.get(0);
          resource.put("title", "课程小组：" + resource.get("group_name"));
        }
        break;
      case projects:
        final List<Map<String, Object>> projectResult = projectMapper.selectMaps(new QueryWrapper<>(new Project())
          .eq(Project.COL_ID, resourceId));
        if (CollectionUtils.isNotEmpty(projectResult)) {
          resource = projectResult.get(0);
          resource.put("title", "项目：" + resource.get("project_name"));
        }
        break;
      case transactions:
        final List<Map<String, Object>> transactionResult = pourrfotTransactionMapper.selectMaps(new QueryWrapper<>(new PourrfotTransaction())
          .eq(PourrfotTransaction.COL_ID, resourceId));
        if (CollectionUtils.isNotEmpty(transactionResult)) {
          resource = transactionResult.get(0);
          resource.put("title", "事务：" + resource.get("title"));
        }
        break;
      case messages:
        final List<Map<String, Object>> messageResult = messageMapper.selectMaps(new QueryWrapper<>(new Message()).eq(Message.COL_ID, resourceId));
        if (CollectionUtils.isNotEmpty(messageResult)) {
          resource = messageResult.get(0);
          resource.put("title", "消息：" + resource.get("title"));
        }
        break;
      default:
        throw new IllegalArgumentException("Not Support oss-file resource type");
    }
    return resource;
  }
}
