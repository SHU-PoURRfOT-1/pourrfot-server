package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.exception.OssFileServiceException;
import cn.edu.shu.pourrfot.server.model.OssFile;
import cn.edu.shu.pourrfot.server.repository.OssFileMapper;
import cn.edu.shu.pourrfot.server.service.OssFileService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CreateSymlinkRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;

/**
 * @author spencercjh
 */
@Slf4j
@Service
public class OssFileServiceImpl extends ServiceImpl<OssFileMapper, OssFile> implements OssFileService {

  @Autowired
  private OSS ossClient;
  @Value("${aliyun.oss.bucket:pourrfot}")
  private String ossBucket;

  @Override
  public String uploadFileWithFilename(MultipartFile file, String filename) {
    return uploadFileWithKey(file, CACHE_FILE_KEY_PREFIX + filename);
  }

  @Override
  public String uploadFileWithKey(MultipartFile file, String key) {
    try {
      ossClient.putObject(ossBucket, key, file.getInputStream());
    } catch (Exception e) {
      log.error("Upload file to OSS failed", e);
      throw new OssFileServiceException(e);
    }
    return setupOssUrl(key);
  }

  /**
   * processes
   * <p>
   * <p>TODO 1. check OssFile#type/resourceId in DB </p>
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
    // TODO 1. check resource

    // 2. check the cache file defined by oss url exist in OSS
    final String key = StringUtils.isNotBlank(entity.getOriginOssUrl()) ?
      OssFileService.getKeyFromOssUrl(entity.getOriginOssUrl()) : entity.getOssKey();
    checkCacheFileExisted(key);
    // 3. create a symbol link
    final String symbolLink = createSymbolLink(entity, key);
    // 4. save OssFile with new OSS info
    return baseMapper.insert(entity
      .setOssKey(symbolLink)
      .setDirectory(setupNewDirectory(entity))
      .setOssUrl(setupOssUrl(symbolLink))
      .setCreateTime(new Date(System.currentTimeMillis()))
      .setUpdateTime(new Date(System.currentTimeMillis()))) == 1;
  }

  private void checkCacheFileExisted(String key) {
    boolean isCacheFileExist;
    try {
      isCacheFileExist = ossClient.doesObjectExist(ossBucket, key);
    } catch (Exception e) {
      log.error("Check OSS object existed failed: {}", key, e);
      throw new OssFileServiceException(e);
    }
    if (!isCacheFileExist) {
      log.error("OSS Cache file: {} doesn't exist", key);
      throw new NotFoundException(String.format("OSS Cache file %s doesn't exist", key));
    }
    log.info("Check OSS object existed: {}", key);
  }

  private String createSymbolLink(OssFile entity, String key) {
    final String symbolLink = setupSymbolLink(entity);
    final ObjectMetadata metadata = new ObjectMetadata();
    if (!CollectionUtils.isEmpty(entity.getMetadata())) {
      entity.getMetadata().forEach((k, v) -> metadata.addUserMetadata(k, String.valueOf(v)));
    }
    final CreateSymlinkRequest createSymlinkRequest = new CreateSymlinkRequest(ossBucket, symbolLink, key);
    try {
      ossClient.createSymlink(createSymlinkRequest);
    } catch (Exception e) {
      log.error("Create OSS Symbol link: {} of {} failed", symbolLink, entity.getOriginOssUrl(), e);
      throw new OssFileServiceException(e);
    }
    log.info("Create OSS Symbol link: {} of {} success", symbolLink, entity.getOriginOssUrl());
    return symbolLink;
  }

  @Override
  public boolean updateById(OssFile entity) {
    // NOT SUPPORT UPDATE
    throw new NotImplementedException();
  }

  @Override
  public Resource getOssFileResource(OssFile ossFile) {
    final OSSObject ossObject;
    try {
      ossObject = ossClient.getObject(ossBucket, ossFile.getOssKey());
    } catch (Exception e) {
      log.error("Get OSS object: {} failed", ossFile.getOssUrl(), e);
      throw new OssFileServiceException(e);
    }
    log.info("Downloading oss file: {}", ossFile.getOssUrl());
    return new InputStreamResource(ossObject.getObjectContent());
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean removeById(Serializable id) {
    final OssFile found = baseMapper.selectById(id);
    if (found == null) {
      return false;
    }
    // 1. delete the symbol link
    deleteObject(found.getOssKey());
    // 2. delete the origin file
    deleteObject(OssFileService.getKeyFromOssUrl(found.getOriginOssUrl()));
    return baseMapper.deleteById(id) == 1;
  }

  private void deleteObject(String key) {
    try {
      ossClient.deleteObject(ossBucket, key);
    } catch (Exception e) {
      log.error("Delete OSS object: {} failed", key, e);
      throw new OssFileServiceException(e);
    }
    log.info("Delete OSS object: {} success", key);
  }

  private String setupOssUrl(String key) {
    return String.format("%s%s/%s", OSS_SCHEME_PREFIX, ossBucket, key);
  }

  private String setupNewDirectory(OssFile entity) {
    return String.format("/%s/%d", entity.getResourceType().getValue(), entity.getResourceId());
  }

  private String setupSymbolLink(OssFile entity) {
    return String.format("%s/%d/%s", entity.getResourceType().getValue(), entity.getResourceId(), entity.getName());
  }
}
