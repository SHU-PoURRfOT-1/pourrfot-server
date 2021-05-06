package cn.edu.shu.pourrfot.server.service;

import cn.edu.shu.pourrfot.server.model.OssFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author spencercjh
 */
public interface OssFileService extends IService<OssFile> {
  String CACHE_FILE_KEY_PREFIX = "caches/";
  String OSS_SCHEME_PREFIX = "oss://";

  /**
   * get directory from oss url (not including bucket)
   * <p>
   * example: oss://bucket/a/b/test.txt, return: /a/b
   * <p>
   * WARNING: it's not reliable method to cover all cases
   *
   * @param ossUrl oss url started with {@link #OSS_SCHEME_PREFIX}
   * @return directory
   */
  static String getDirectoryFromOssUrl(final String ossUrl) {
    // TODO: use regex
    if (!ossUrl.startsWith(OSS_SCHEME_PREFIX)) {
      throw new IllegalArgumentException(String.format("Illegal OSS url: %s", ossUrl));
    }
    // remove oss scheme
    String result = ossUrl.substring(OSS_SCHEME_PREFIX.length() + 1);
    // remove bucket
    result = result.substring(result.indexOf("/"));
    final int theLastSlash = result.lastIndexOf("/");
    // root
    if (theLastSlash == 0) {
      return "/";
    }
    return result.substring(0, theLastSlash);
  }

  /**
   * get key from oss url (not including bucket)
   * <p>
   * example: oss://bucket/a/b/test.txt, return: /a/b/test.txt
   * <p>
   * WARNING: it's not reliable method to cover all cases
   *
   * @param ossUrl oss url started with {@link #OSS_SCHEME_PREFIX}
   * @return key
   */
  static String getKeyFromOssUrl(final String ossUrl) {
    // TODO: use regex
    if (!ossUrl.startsWith(OSS_SCHEME_PREFIX)) {
      throw new IllegalArgumentException(String.format("Illegal OSS url: %s", ossUrl));
    }
    // remove oss scheme
    String result = ossUrl.substring(OSS_SCHEME_PREFIX.length() + 1);
    // remove bucket
    return result.substring(result.indexOf("/") + 1);
  }

  /**
   * Files without a specified Key will be uploaded to the caches/ directory
   *
   * @param file     file
   * @param filename filename without directory end with file type suffix
   * @return oss url with oss:// prefix
   * @throws Throwable failed
   */
  String uploadFileWithFilename(MultipartFile file, String filename) throws Throwable;

  /**
   * upload a file to OSS with a specific key
   *
   * @param file file
   * @param key  key end with file type suffix
   * @return oss url with oss:// prefix
   * @throws Throwable failed
   */
  String uploadFileWithKey(MultipartFile file, String key) throws Throwable;

  /**
   * get OSS object stream by {@link OssFile#getOssKey()}
   *
   * @param ossFile oss file
   * @return stream
   */
  Resource getOssFileResource(OssFile ossFile);
}
