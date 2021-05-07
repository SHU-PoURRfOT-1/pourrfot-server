package cn.edu.shu.pourrfot.server.service;

import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.exception.OssFileServiceException;
import cn.edu.shu.pourrfot.server.model.OssFile;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CreateSymlinkRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author spencercjh
 */
@Slf4j
@Service
public class OssServiceImpl implements OssService {
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

  @Override
  public boolean checkOssObjectExisted(String key) {
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
    return true;
  }

  @Override
  public String createSymbolLink(String originKey, String symbolLink, Map<String, Object> metadata) {
    final ObjectMetadata objectMetadata = new ObjectMetadata();
    if (!CollectionUtils.isEmpty(metadata)) {
      metadata.forEach((k, v) -> objectMetadata.addUserMetadata(k, String.valueOf(v)));
    }
    final CreateSymlinkRequest createSymlinkRequest = new CreateSymlinkRequest(ossBucket, symbolLink, originKey);
    try {
      ossClient.createSymlink(createSymlinkRequest);
    } catch (Exception e) {
      log.error("Create OSS Symbol link: {} of {} failed", symbolLink, originKey, e);
      throw new OssFileServiceException(e);
    }
    log.info("Create OSS Symbol link: {} of {} success", symbolLink, originKey);
    return symbolLink;
  }

  @Override
  public boolean deleteOssObject(String key) {
    try {
      ossClient.deleteObject(ossBucket, key);
    } catch (Exception e) {
      log.error("Delete OSS object: {} failed", key, e);
      throw new OssFileServiceException(e);
    }
    log.info("Delete OSS object: {} success", key);
    return true;
  }

  @Override
  public String setupOssUrl(String key) {
    return String.format("%s%s/%s", OSS_SCHEME_PREFIX, ossBucket, key);
  }
}
