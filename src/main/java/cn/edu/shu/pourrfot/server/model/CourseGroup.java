package cn.edu.shu.pourrfot.server.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-CourseGroup")
@Accessors(chain = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "course_group")
public class CourseGroup {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_COURSE_ID = "course_id";
  public static final String COL_GROUP_NAME = "group_name";
  public static final String COL_PROFILE_PHOTO = "profile_photo";
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
  @TableField(value = "course_id")
  @ApiModelProperty(required = true)
  @NotNull
  private Integer courseId;
  @TableField(value = "group_name")
  @ApiModelProperty()
  private String groupName;
  @TableField(value = "profile_photo")
  @ApiModelProperty()
  private String profilePhoto;
}
