package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.service.CourseGroupService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import java.util.Optional;

/**
 * @author spencercjh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/courses/{courseId}/groups")
public class CourseGroupController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private CourseGroupService courseGroupService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<CourseGroup>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                @RequestParam(required = false, defaultValue = "10") Integer size,
                                                @PathVariable @NotNull Integer courseId,
                                                @RequestParam(required = false) String groupName) {
    QueryWrapper<CourseGroup> query = Wrappers.query(new CourseGroup());
    if (courseId != null) {
      query = query.eq(CourseGroup.COL_COURSE_ID, courseId);
    }
    if (StringUtils.isNotBlank(groupName)) {
      query = query.like(CourseGroup.COL_GROUP_NAME, groupName);
    }
    return ResponseEntity.ok(courseGroupService.page(new Page<>(current, size), query));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CourseGroup> detail(@PathVariable @NotNull Integer courseId,
                                            @PathVariable @NotNull Integer id) {
    return ResponseEntity.of(Optional.ofNullable(courseGroupService.getById(id)));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<CourseGroup> create(@NotNull @RequestBody @Validated CourseGroup courseGroup) {
    courseGroupService.save(courseGroup);
    return ResponseEntity.created(
      URI.create(String.format("%s/courses/%d/groups/%d", contextPath, courseGroup.getCourseId(), courseGroup.getId())))
      .body(courseGroup);
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CourseGroup> update(@PathVariable @NotNull Integer courseId,
                                            @PathVariable @NotNull Integer id,
                                            @RequestBody @Validated @NotNull CourseGroup courseGroup) {
    courseGroupService.updateById(courseGroup.setId(id).setCourseId(courseId));
    return ResponseEntity.ok(courseGroup);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer courseId, @PathVariable @NotNull Integer id) {
    return courseGroupService.removeById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
