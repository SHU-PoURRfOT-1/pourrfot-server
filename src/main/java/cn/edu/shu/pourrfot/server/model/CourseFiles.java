package cn.edu.shu.pourrfot.server.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-CourseFiles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pourrfot.course_files")
@Accessors(chain = true)
public class CourseFiles {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_COURSE_ID = "course_id";
  public static final String COL_FILE_ID = "file_id";
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
  @TableField(value = "course_id")
  @ApiModelProperty(value = "")
  private Integer courseId;
  @TableField(value = "file_id")
  @ApiModelProperty(value = "")
  private Integer fileId;
}
