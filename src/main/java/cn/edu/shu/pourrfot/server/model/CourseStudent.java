package cn.edu.shu.pourrfot.server.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-CourseStudent")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "course_student")
public class CourseStudent {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_STUDENT_ID = "student_id";
  public static final String COL_COURSE_ID = "course_id";
  public static final String COL_PART_ONE_SCORE = "part_one_score";
  public static final String COL_PART_ONE_SCORE_WEIGHT = "part_one_score_weight";
  public static final String COL_PART_TWO_SCORE = "part_two_score";
  public static final String COL_PART_TWO_SCORE_WEIGHT = "part_two_score_weight";
  public static final String COL_PART_THREE_SCORE = "part_three_score";
  public static final String COL_PART_THREE_SCORE_WEIGHT = "part_three_score_weight";
  public static final String COL_PART_FOUR_SCORE = "part_four_score";
  public static final String COL_PART_FOUR_SCORE_WEIGHT = "part_four_score_weight";
  public static final String COL_PART_FIVE_SCORE = "part_five_score";
  public static final String COL_PART_FIVE_SCORE_WEIGHT = "part_five_score_weight";
  public static final String COL_TOTAL_SCORE = "total_score";
  @TableId(value = "id", type = IdType.AUTO)
  @ApiModelProperty(value = "")
  private Integer id;
  @TableField(value = "create_time")
  @ApiModelProperty(value = "")
  private Date createTime;
  @TableField(value = "update_time")
  @ApiModelProperty(value = "")
  private Date updateTime;
  @TableField(value = "student_id")
  @ApiModelProperty(value = "")
  private Integer studentId;
  @TableField(value = "course_id")
  @ApiModelProperty(value = "")
  private Integer courseId;
  @TableField(value = "part_one_score")
  @ApiModelProperty(value = "")
  private Long partOneScore;
  @TableField(value = "part_one_score_weight")
  @ApiModelProperty(value = "")
  private Long partOneScoreWeight;
  @TableField(value = "part_two_score")
  @ApiModelProperty(value = "")
  private Long partTwoScore;
  @TableField(value = "part_two_score_weight")
  @ApiModelProperty(value = "")
  private Long partTwoScoreWeight;
  @TableField(value = "part_three_score")
  @ApiModelProperty(value = "")
  private Long partThreeScore;
  @TableField(value = "part_three_score_weight")
  @ApiModelProperty(value = "")
  private Long partThreeScoreWeight;
  @TableField(value = "part_four_score")
  @ApiModelProperty(value = "")
  private Long partFourScore;
  @TableField(value = "part_four_score_weight")
  @ApiModelProperty(value = "")
  private Long partFourScoreWeight;
  @TableField(value = "part_five_score")
  @ApiModelProperty(value = "")
  private Long partFiveScore;
  @TableField(value = "part_five_score_weight")
  @ApiModelProperty(value = "")
  private Long partFiveScoreWeight;
  @TableField(value = "total_score")
  @ApiModelProperty(value = "")
  private Long totalScore;
}
