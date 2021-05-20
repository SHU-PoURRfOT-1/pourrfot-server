package cn.edu.shu.pourrfot.server.model.dto;

import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.model.CourseStudent;
import com.baomidou.mybatisplus.annotation.Version;
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
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-dto-CompleteStudent")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CompleteCourseStudent {
  @ApiModelProperty()
  private Integer id;
  @ApiModelProperty()
  private Date createTime;
  @ApiModelProperty()
  @Version
  private Date updateTime;
  @ApiModelProperty()
  @NotNull
  private Integer studentId;
  @ApiModelProperty()
  @NotNull
  private String studentName;
  @ApiModelProperty()
  @NotNull
  private Course course;
  @ApiModelProperty
  private CourseGroup group;
  @ApiModelProperty()
  @Max(100_00)
  @Min(0)
  private Long totalScore;
  @ApiModelProperty
  private List<?> detailScore;

  public static CompleteCourseStudent of(CourseStudent courseStudent, Course course, CourseGroup courseGroup) {
    return CompleteCourseStudent.builder()
      .id(courseStudent.getId())
      .createTime(courseStudent.getCreateTime())
      .updateTime(courseStudent.getUpdateTime())
      .studentId(courseStudent.getStudentId())
      .studentName(courseStudent.getStudentName())
      .course(course)
      .group(courseGroup)
      .totalScore(courseStudent.getTotalScore())
      .detailScore(courseStudent.getDetailScore())
      .build();
  }
}
