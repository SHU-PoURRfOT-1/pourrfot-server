package cn.edu.shu.pourrfot.server.model;

import com.baomidou.mybatisplus.annotation.*;
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

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-ProjectUser")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "project_user")
@Accessors(chain = true)
public class ProjectUser {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_PROJECT_ID = "project_id";
  public static final String COL_USER_ID = "user_id";
  public static final String COL_ROLE_NAME = "role_name";
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
  @TableField(value = "project_id")
  @ApiModelProperty(value = "")
  @NotNull
  private Integer projectId;
  @TableField(value = "user_id")
  @ApiModelProperty(value = "")
  @NotNull
  private Integer userId;
  /**
   * role in the project
   */
  @TableField(value = "role_name")
  @ApiModelProperty(value = "role in the project")
  @NotBlank
  private String roleName;
}
