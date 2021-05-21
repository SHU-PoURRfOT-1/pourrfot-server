package cn.edu.shu.pourrfot.server.model;

import cn.edu.shu.pourrfot.server.enums.GroupingMethodEnum;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
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
import java.util.List;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-Course")
@Accessors(chain = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(autoResultMap = true, value = "course")
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
  public static final String COL_GROUPING_METHOD = "grouping_method";
  public static final String COL_GROUP_SIZE = "group_size";

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
  @TableField(value = "teacher_id")
  @ApiModelProperty(required = true)
  @NotNull
  private Integer teacherId;
  @TableField(value = "course_code")
  @ApiModelProperty(required = true, notes = "Unique")
  @NotEmpty
  private String courseCode;
  @TableField(value = "course_name")
  @ApiModelProperty(required = true)
  @NotEmpty
  private String courseName;
  @TableField(value = "class_time")
  @ApiModelProperty()
  private String classTime;
  @TableField(value = "class_location")
  @ApiModelProperty()
  private String classLocation;
  @TableField(value = "term")
  @ApiModelProperty()
  private String term;
  @TableField(value = "profile_photo")
  @ApiModelProperty()
  private String profilePhoto;
  @TableField(value = "grouping_method")
  @ApiModelProperty(example = "NOT_GROUPING")
  private GroupingMethodEnum groupingMethod;
  @TableField(value = "group_size")
  @ApiModelProperty(example = "0")
  private Integer groupSize;
  @TableField(value = "score_structure", typeHandler = JacksonTypeHandler.class)
  @ApiModelProperty
  private List<?> scoreStructure;
}
