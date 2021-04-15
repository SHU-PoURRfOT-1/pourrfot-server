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
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-CourseGroupStudent")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "course_group_student")
public class CourseGroupStudent {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_GROUP_ID = "group_id";
  public static final String COL_COURSE_ID = "course_id";
  public static final String COL_STUDENT_ID = "student_id";
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
  @TableField(value = "group_id")
  @ApiModelProperty(value = "")
  private Integer groupId;
  @TableField(value = "course_id")
  @ApiModelProperty(value = "")
  private Integer courseId;
  @TableField(value = "student_id")
  @ApiModelProperty(value = "")
  private Integer studentId;
}
