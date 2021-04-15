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
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-Course")
@Accessors(chain = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "course")
public class Course {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_TEACHER_ID = "teacher_id";
  public static final String COL_COURSE_CODE = "course_code";
  public static final String COL_COURSE_NAME = "course_name";
  public static final String COL_CLASS_TIME = "class_time";
  public static final String COL_CLASS_LOCATION = "class_location";
  public static final String COL_TERM = "term";
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
  @TableField(value = "teacher_id")
  @ApiModelProperty(value = "")
  @NotNull
  private Integer teacherId;
  @TableField(value = "course_code")
  @ApiModelProperty(value = "")
  @NotEmpty
  private String courseCode;
  @TableField(value = "course_name")
  @ApiModelProperty(value = "")
  @NotEmpty
  private String courseName;
  @TableField(value = "class_time")
  @ApiModelProperty(value = "")
  private String classTime;
  @TableField(value = "class_location")
  @ApiModelProperty(value = "")
  private String classLocation;
  @TableField(value = "term")
  @ApiModelProperty(value = "")
  private String term;
  @TableField(value = "profile_photo")
  @ApiModelProperty(value = "")
  private String profilePhoto;
}
