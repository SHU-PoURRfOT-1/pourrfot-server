package cn.edu.shu.pourrfot.server.model;

import cn.edu.shu.pourrfot.server.enums.ResourceTypeEnum;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-OssFile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "oss_file", autoResultMap = true)
@Accessors(chain = true)
public class OssFile {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_NAME = "name";
  public static final String COL_METADATA = "metadata";
  public static final String COL_RESOURCE_TYPE = "resource_type";
  public static final String COL_RESOURCE_ID = "resource_id";
  public static final String COL_DIRECTORY = "directory";
  public static final String COL_OSS_URL = "oss_url";
  public static final String COL_ORIGIN_OSS_URL = "origin_oss_url";
  public static final String COL_OWNER_ID = "owner_id";

  @TableId(value = "id", type = IdType.AUTO)
  @ApiModelProperty()
  private Integer id;
  @TableField(value = "create_time")
  @ApiModelProperty()
  private Date createTime;
  @TableField(value = "update_time")
  @ApiModelProperty()
  @Version
  private Date updateTime;
  @TableField(value = "`name`")
  @ApiModelProperty(example = "test.txt", required = true)
  @NotBlank
  private String name;
  @ApiModelProperty(example = "[{\\\"type\\\":\\\"danger\\\",\\\"label\\\":\\\"加急\\\"},{\\\"type\\\":\\\"success\\\",\\\"label\\\":\\\"success\\\"},{\\\"type\\\":\\\"warning\\\",\\\"label\\\":\\\"需要批阅\\\"}]")
  @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
  private Map<String, Object> metadata;
  @TableField(value = "resource_type")
  @ApiModelProperty(required = true)
  @NotNull
  private ResourceTypeEnum resourceType;
  /**
   * course/project/transaction/message id
   */
  @TableField(value = "resource_id")
  @ApiModelProperty(value = "course/project/transaction/message id", required = true)
  @NotNull
  private Integer resourceId;
  /**
   * Unix-like directory
   */
  @TableField(value = "directory")
  @ApiModelProperty(value = "SET BY SERVER")
  private String directory;
  @TableField(value = "oss_key")
  @ApiModelProperty(value = "SET BY SERVER")
  private String ossKey;
  /**
   * symbol link location
   */
  @TableField(value = "oss_url")
  @ApiModelProperty(value = "SET BY SERVER")
  private String ossUrl;
  /**
   * origin file location
   */
  @TableField(value = "origin_oss_url")
  @ApiModelProperty(required = true)
  @NotBlank
  private String originOssUrl;
  /**
   * uploader userid
   */
  @TableField(value = "owner_id")
  @ApiModelProperty(value = "uploader userid; SET BY SERVER")
  private Integer ownerId;
}
