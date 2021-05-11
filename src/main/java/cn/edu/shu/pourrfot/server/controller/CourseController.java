package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * @author spencercjh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/courses")
public class CourseController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private CourseService courseService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<Course>>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
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
      Result.normalOk("Get courses page success", courseService.page(new Page<>(current, size), query)));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find course with the specific id", response = Result.class)})
  public ResponseEntity<Result<Course>> detail(@PathVariable @NotNull Integer id) {
    final Course found = courseService.getById(id);
    return found == null ?
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Not found course with the specific id")) :
      ResponseEntity.ok(Result.normalOk("Get course detail success", found));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<Result<Course>> create(@NotNull @RequestBody @Validated Course course) {
    courseService.save(course);
    return ResponseEntity.created(URI.create(String.format("%s/courses/%d", contextPath, course.getId())))
      .body(Result.createdOk("Create course success", course));
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Course>> update(@PathVariable @NotNull Integer id,
                                               @RequestBody @Validated @NotNull Course course) {
    courseService.updateById(course.setId(id));
    return ResponseEntity.ok(Result.normalOk("Update course success", course));
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @ApiResponses({@ApiResponse(code = 204, message = "Delete course success", response = Result.class),
    @ApiResponse(code = 404, message = "Can't find the course with the specific id to delete", response = Result.class)})
  public ResponseEntity<Result<?>> delete(@PathVariable @NotNull Integer id) {
    return courseService.removeById(id) ? ResponseEntity.status(HttpStatus.NO_CONTENT)
      .body(Result.deleteOk("Delete course success")) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't find the course with the specific id to delete"));
  }
}
