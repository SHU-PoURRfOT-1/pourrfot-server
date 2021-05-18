package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.enums.ResourceTypeEnum;
import cn.edu.shu.pourrfot.server.model.OssFile;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.service.OssFileService;
import cn.edu.shu.pourrfot.server.service.OssService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
  @Autowired
  private OssService ossService;
  @Value("${server.servlet.contextPath}")
  private String contextPath;

  @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<OssFile>>> list(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                    @RequestParam(required = false, defaultValue = "10") Integer size,
                                                    @RequestParam(required = false) ResourceTypeEnum resourceType,
                                                    @RequestParam(required = false) Integer resourceId,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String directory,
                                                    @RequestParam(required = false) Integer ownerId) {
    QueryWrapper<OssFile> query = Wrappers.query(new OssFile());
    if (resourceType != null && resourceId != null) {
      query = query.eq(OssFile.COL_RESOURCE_TYPE, resourceType).eq(OssFile.COL_RESOURCE_ID, resourceId);
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
    return ResponseEntity.ok(Result.normalOk("Get oss-files page success",
      ossFileService.page(new Page<>(current, size), query)));
  }

  @GetMapping(value = "/detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find oss-file with the specific id", response = Result.class)})
  public ResponseEntity<Result<OssFile>> detail(@PathVariable Integer id) {
    final OssFile found = ossFileService.getById(id);
    return found != null ? ResponseEntity.ok(Result.normalOk("Get oss-file detail success", found)) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't found oss-file with the specific id"));
  }

  @GetMapping(value = "/detail/{id}/stream")
  @ApiResponses({@ApiResponse(code = 200, message = "Start download oss-file with the specific id"),
    @ApiResponse(code = 404, message = "Can't found oss-file with the specific id to download", response = Result.class)})
  public ResponseEntity<?> downloadFile(@PathVariable Integer id) {
    final OssFile found = ossFileService.getById(id);
    if (found == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Result.notFound("Not found oss-file with the specific id to download"));
    }
    // Simple handling the content type
    final Resource ossFileResource = ossService.getOssFileResource(found);
    // process chinese filename
    final String filename = URLEncoder.encode(found.getName(), StandardCharsets.UTF_8)
      .replaceAll("\\+", "%20");
    return ResponseEntity.ok()
      .contentType(MediaType.APPLICATION_OCTET_STREAM)
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
      .body(ossFileResource);
  }

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<OssFile>> create(@NotNull @RequestBody @Validated OssFile ossFile) {
    // origin oss url in header#location is encoded. It needs to be decoded
    ossFileService.save(ossFile.setOriginOssUrl(URLDecoder.decode(ossFile.getOriginOssUrl(), StandardCharsets.UTF_8)));
    return ResponseEntity.ok()
      .location(URI.create(String.format("%s/files/detail/%d", contextPath, ossFile.getId())))
      .body(Result.createdOk("Create oss-file success, please pay attention to the LOCATION in headers",
        ossFile));
  }

  @PostMapping(value = "/cache", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<String>> uploadFile(@NotNull @RequestPart MultipartFile file,
                                                   @RequestPart(required = false) String filename) {
    final String ossUrl = ossService.uploadFileWithFilename(file, StringUtils.isBlank(filename) ?
      file.getOriginalFilename() : filename);
    return ResponseEntity.created(URI.create(ossUrl)).body(Result.normalOk("Upload file success", ossUrl));
  }

  @PostMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 200, message = "Delete oss-url success", response = Result.class),
    @ApiResponse(code = 404, message = "Can't find the oss-url with the specific id to delete", response = Result.class)})
  public ResponseEntity<Result<?>> delete(@PathVariable @NotNull Integer id) {
    return ossFileService.removeById(id) ? ResponseEntity.ok()
      .body(Result.deleteOk("Delete oss-url success")) :
      ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Result.notFound("Can't find the oss-url with the specific id to delete"));
  }
}
