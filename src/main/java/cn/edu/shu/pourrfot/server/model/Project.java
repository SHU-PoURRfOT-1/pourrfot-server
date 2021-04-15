package cn.edu.shu.pourrfot.server.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-Project")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "project")
public class Project {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_PROJECT_NAME = "project_name";
  public static final String COL_PROJECT_CODE = "project_code";
  public static final String COL_OWNER_ID = "owner_id";
  public static final String COL_PROFILE_PHOTO = "profile_photo";
  @TableId(value = "id", type = IdType.AUTO)
  @ApiModelProperty(value = "")
  private Integer id;
  @TableField(value = "create_time")
  @ApiModelProperty(value = "")
  private Date createTime;
  @TableField(value = "update_time")
  @ApiModelProperty(value = "")
  @Version
  private Date updateTime;
  @TableField(value = "project_name")
  @ApiModelProperty(value = "")
  private String projectName;
  @TableField(value = "project_code")
  @ApiModelProperty(value = "")
  private String projectCode;
  @TableField(value = "owner_id")
  @ApiModelProperty(value = "")
  private Integer ownerId;
  @TableField(value = "profile_photo")
  @ApiModelProperty(value = "")
  private String profilePhoto;
}
