package cn.edu.shu.pourrfot.server.model.dto;

import cn.edu.shu.pourrfot.server.model.OssFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-CompleteOssFile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CompleteOssFile {
  @ApiModelProperty()
  private Integer id;
  @ApiModelProperty()
  private Date createTime;
  @ApiModelProperty()
  private Date updateTime;
  @ApiModelProperty(example = "test.txt")
  private String name;
  @ApiModelProperty()
  private List<?> metadata;
  /**
   * Unix-like directory
   */
  @ApiModelProperty(value = "SET BY SERVER")
  private String directory;
  @ApiModelProperty(value = "SET BY SERVER")
  private String ossKey;
  /**
   * symbol link location
   */
  @ApiModelProperty(value = "SET BY SERVER")
  private String ossUrl;
  /**
   * origin file location
   */
  @ApiModelProperty()
  @NotBlank
  private String originOssUrl;
  /**
   * uploader userid
   */
  @ApiModelProperty(value = "uploader user")
  private Map<String, Object> owner;
  @ApiModelProperty(notes = "get by resource type/resource id")
  private Map<String, Object> resource;

  public static CompleteOssFile of(OssFile ossFile, Map<String, Object> owner, Map<String, Object> resource) {
    return CompleteOssFile.builder()
      .id(ossFile.getId())
      .name(ossFile.getName())
      .metadata(ossFile.getMetadata())
      .directory(ossFile.getDirectory())
      .ossKey(ossFile.getOssKey())
      .ossUrl(ossFile.getOssUrl())
      .originOssUrl(ossFile.getOriginOssUrl())
      .createTime(ossFile.getCreateTime())
      .updateTime(ossFile.getUpdateTime())
      .owner(owner)
      .resource(resource)
      .build();
  }
}
