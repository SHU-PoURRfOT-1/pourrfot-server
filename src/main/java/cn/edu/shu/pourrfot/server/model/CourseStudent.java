package cn.edu.shu.pourrfot.server.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-CourseStudent")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "course_student", autoResultMap = true)
@Accessors(chain = true)
public class CourseStudent {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_STUDENT_ID = "student_id";
  public static final String COL_STUDENT_NAME = "student_name";
  public static final String COL_COURSE_ID = "course_id";
  public static final String COL_GROUP_ID = "group_id";
  public static final String COL_TOTAL_SCORE = "total_score";
  public static final String COL_SCORE_STRUCTURE = "score_structure";
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
  @TableField(value = "student_id")
  @ApiModelProperty(required = true)
  @NotNull
  private Integer studentId;
  @TableField(value = "student_name")
  @ApiModelProperty(required = true)
  @NotNull
  private String studentName;
  @TableField(value = "course_id")
  @ApiModelProperty(required = true)
  @NotNull
  private Integer courseId;
  @TableField(value = "group_id")
  @ApiModelProperty
  private Integer groupId;
  @TableField(value = "total_score")
  @ApiModelProperty()
  @Max(100_00)
  @Min(0)
  private Long totalScore;
  @TableField(value = "detail_score", typeHandler = JacksonTypeHandler.class)
  @ApiModelProperty
  private List<?> detailScore;
}
