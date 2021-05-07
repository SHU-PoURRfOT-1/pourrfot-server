package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.enums.ResourceTypeEnum;
import cn.edu.shu.pourrfot.server.model.OssFile;
import cn.edu.shu.pourrfot.server.service.OssFileService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * NOT SUPPORT UPDATE AND ONlY SUPPORT DELETE
 *
 * @author spencercjh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/files")
public class OssFileController {
  @Autowired
  private OssFileService ossFileService;
  @Value("${server.servlet.contextPath}")
  private String contextPath;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<OssFile>> list(@RequestParam(required = false, defaultValue = "1") Integer current,
                                            @RequestParam(required = false, defaultValue = "10") Integer size,
                                            @RequestParam(required = false) ResourceTypeEnum resourceType,
                                            @RequestParam(required = false) Integer resourceId,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) String directory,
                                            @RequestParam(required = false) Integer ownerId) {
    QueryWrapper<OssFile> query = Wrappers.query(new OssFile());
    if (resourceType != null) {
      query = query.eq(OssFile.COL_RESOURCE_TYPE, resourceType);
    }
    if (resourceId != null) {
      query = query.eq(OssFile.COL_RESOURCE_ID, resourceId);
    }
    if (StringUtils.isNotBlank(name)) {
      query = query.like(OssFile.COL_NAME, name.trim());
    }
    if (StringUtils.isNotBlank(directory)) {
      query = query.eq(OssFile.COL_DIRECTORY, directory.trim());
    }
    if (ownerId != null) {
      query = query.eq(OssFile.COL_OWNER_ID, ownerId);
    }
    return ResponseEntity.ok(ossFileService.page(new Page<>(current, size), query));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<OssFile> detail(@PathVariable Integer id) {
    return ResponseEntity.of(Optional.ofNullable(ossFileService.getById(id)));
  }

  @GetMapping(value = "/{id}/stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> downloadFile(@PathVariable Integer id) {
    final OssFile found = ossFileService.getById(id);
    if (found == null) {
      return ResponseEntity.notFound().build();
    }
    // Simple handling the content type and other header
    return ResponseEntity.ok()
      .contentType(MediaType.APPLICATION_OCTET_STREAM)
      .body(ossFileService.getOssFileResource(found));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<OssFile> create(@NotNull @RequestBody @Validated OssFile ossFile) {
    // origin oss url in header#location is encoded. It needs to be decoded
    ossFileService.save(ossFile.setOriginOssUrl(URLDecoder.decode(ossFile.getOriginOssUrl(), StandardCharsets.UTF_8)));
    return ResponseEntity.created(URI.create(String.format("%s/files/%d", contextPath, ossFile.getId()))).body(ossFile);
  }

  @PostMapping("/cache")
  public ResponseEntity<String> uploadFile(@NotNull @RequestParam MultipartFile file,
                                           @NotBlank @RequestParam(required = false) String fileName) {
    final String ossUrl;
    try {
      ossUrl = ossFileService.uploadFileWithFilename(file, StringUtils.isBlank(fileName) ?
        file.getOriginalFilename() : fileName);
    } catch (Throwable t) {
      log.error("Upload file to OSS failed: {}", file.getName(), t);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return ResponseEntity.created(URI.create(ossUrl)).build();
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer id) {
    return ossFileService.removeById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
