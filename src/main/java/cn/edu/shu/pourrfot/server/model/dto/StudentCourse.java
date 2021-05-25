package cn.edu.shu.pourrfot.server.model.dto;

import cn.edu.shu.pourrfot.server.enums.GroupingMethodEnum;
import cn.edu.shu.pourrfot.server.model.*;
import com.baomidou.mybatisplus.annotation.TableName;
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
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-dtl-StudentCourse")
@Accessors(chain = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(autoResultMap = true, value = "course")
public class StudentCourse {
  @ApiModelProperty()
  private Integer courseId;
  @ApiModelProperty()
  private Date createTime;
  @ApiModelProperty()
  @Version
  private Date updateTime;
  @ApiModelProperty(notes = "Unique")
  @NotEmpty
  private String courseCode;
  @ApiModelProperty()
  @NotEmpty
  private String courseName;
  @ApiModelProperty()
  private String classTime;
  @ApiModelProperty()
  private String classLocation;
  @ApiModelProperty()
  private String term;
  @ApiModelProperty()
  private String profilePhoto;
  @ApiModelProperty()
  private GroupingMethodEnum groupingMethod;
  @ApiModelProperty()
  private Integer groupSize;
  @ApiModelProperty
  private List<ScoreItem> scoreStructure;
  @ApiModelProperty
  private List<ScoreItem> detailScore;
  @ApiModelProperty()
  @Max(100_00)
  @Min(0)
  private Long totalScore;
  @ApiModelProperty()
  private CourseGroup courseGroup;
  @ApiModelProperty
  private PourrfotUser teacher;

  public static StudentCourse of(Course course, CourseStudent courseStudent, CourseGroup courseGroup, PourrfotUser teacher) {
    final StudentCourseBuilder builder = StudentCourse.builder();
    if (courseStudent != null) {
      builder.detailScore((List<ScoreItem>) courseStudent.getDetailScore())
        .totalScore(courseStudent.getTotalScore());
    }
    return builder
      .teacher(teacher)
      .courseId(course.getId())
      .createTime(course.getCreateTime())
      .updateTime(course.getUpdateTime())
      .courseCode(course.getCourseCode())
      .courseName(course.getCourseName())
      .classTime(course.getClassTime())
      .classLocation(course.getClassLocation())
      .term(course.getTerm())
      .profilePhoto(course.getProfilePhoto())
      .groupingMethod(course.getGroupingMethod())
      .groupSize(course.getGroupSize())
      .scoreStructure((List<ScoreItem>) course.getScoreStructure())
      .courseGroup(courseGroup)
      .build();
  }
}
