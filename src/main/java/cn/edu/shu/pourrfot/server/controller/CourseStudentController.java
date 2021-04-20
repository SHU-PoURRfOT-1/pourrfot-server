package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.CourseStudent;
import cn.edu.shu.pourrfot.server.service.CourseStudentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Optional;

/**
 * {id} in this class mean course-student entity's id but not student's id
 *
 * @author spencercjh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/courses/{courseId}/students")
public class CourseStudentController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private CourseStudentService courseStudentService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<CourseStudent>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size,
                                                  @PathVariable @NotNull Integer courseId,
                                                  @RequestParam(required = false) Integer groupId) {
    QueryWrapper<CourseStudent> query = Wrappers.query(new CourseStudent());
    if (courseId != null) {
      query = query.eq(CourseStudent.COL_COURSE_ID, courseId);
    }
    if (groupId != null) {
      query = query.eq(CourseStudent.COL_GROUP_ID, groupId);
    }
    return ResponseEntity.ok(courseStudentService.page(new Page<>(current, size), query));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CourseStudent> detail(@PathVariable @NotNull Integer courseId,
                                              @PathVariable @NotNull Integer id) {
    return ResponseEntity.of(Optional.ofNullable(courseStudentService.getById(id)));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<CourseStudent> create(@NotNull @RequestBody @Validated CourseStudent courseStudent) {
    courseStudentService.save(courseStudent);
    return ResponseEntity.created(
      URI.create(String.format("%s/courses/%d/students/%d", contextPath, courseStudent.getCourseId(), courseStudent.getId())))
      .body(courseStudent);
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CourseStudent> update(@PathVariable @NotNull Integer courseId,
                                              @PathVariable @NotNull Integer id,
                                              @RequestBody @Validated @NotNull CourseStudent courseStudent) {
    courseStudentService.updateById(courseStudent.setId(id));
    return ResponseEntity.ok(courseStudent);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer courseId, @PathVariable @NotNull Integer id) {
    return courseStudentService.removeById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
