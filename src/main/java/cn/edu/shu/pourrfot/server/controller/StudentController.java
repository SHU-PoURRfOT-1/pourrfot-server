package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.model.dto.StudentCourse;
import cn.edu.shu.pourrfot.server.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author spencercjh
 */
@RestController
@RequestMapping("/students/current")
@Slf4j
@Validated
@Api(value = "Student's own resource CRUD", authorizations = {@Authorization("student")}, tags = "Student's Own Resource")
public class StudentController {
  @Autowired
  private CourseService courseService;

  @PreAuthorize("hasAnyAuthority('student')")
  @ApiOperation(value = "student's courses page",
    notes = "admin users can access all courses;\n" +
      "student users can only access their own courses.")
  @GetMapping(value = "/courses", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<StudentCourse>>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                          @RequestParam(required = false, defaultValue = "10") Integer size,
                                                          @RequestParam(required = false) Integer teacherId,
                                                          @RequestParam(required = false) String courseCode,
                                                          @RequestParam(required = false) String courseName,
                                                          @RequestParam(required = false) String term) {
    QueryWrapper<Course> query = Wrappers.query(new Course());
    if (teacherId != null) {
      query = query.eq(Course.COL_TEACHER_ID, teacherId);
    }
    if (StringUtils.isNotBlank(courseCode)) {
      query = query.eq(Course.COL_COURSE_CODE, courseCode.trim());
    }
    if (StringUtils.isNotBlank(courseName)) {
      query = query.eq(Course.COL_COURSE_NAME, courseName.trim());
    }
    if (StringUtils.isNotBlank(term)) {
      query = query.eq(Course.COL_TERM, term.trim());
    }
    return ResponseEntity.ok(
      Result.normalOk("Get student courses page success",
        courseService.studentCoursePage(new Page<>(current, size), query)));
  }
}
