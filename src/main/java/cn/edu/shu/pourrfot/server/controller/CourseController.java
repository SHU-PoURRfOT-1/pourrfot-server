package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.Course;
import cn.edu.shu.pourrfot.server.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Optional;

/**
 * @author spencercjh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/courses")
public class CourseController {
  @Autowired
  private CourseService courseService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<Course>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
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
    return ResponseEntity.ok(courseService.page(new Page<>(current, size), query));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Course> detail(@PathVariable @NotNull Integer id) {
    return ResponseEntity.of(Optional.ofNullable(courseService.getById(id)));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<Course> create(@NotNull @RequestBody @Validated Course course) {
    courseService.save(course);
    return ResponseEntity.created(URI.create("/api/courses/" + course.getId())).body(course);
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Course> update(@PathVariable @NotNull Integer id,
                                       @RequestBody @Validated @NotNull Course course) {
    courseService.updateById(course.setId(id));
    return ResponseEntity.ok(course);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer id) {
    return courseService.removeById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
