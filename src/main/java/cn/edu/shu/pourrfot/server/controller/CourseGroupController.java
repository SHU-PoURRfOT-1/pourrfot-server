package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.CourseGroup;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.service.CourseGroupService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
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
@RequestMapping("/courses/{courseId}/groups")
@Api(value = "Course-Groups CRUD", authorizations = {@Authorization("admin"), @Authorization("teacher"),
  @Authorization("student")}, tags = "Course-Groups")
public class CourseGroupController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private CourseGroupService courseGroupService;

  @ApiOperation(value = "course-groups page",
    notes = "admin users can access all courses-groups; teacher and student users can only access their own course's groups; student can only get one group max particularly")
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<CourseGroup>>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                        @RequestParam(required = false, defaultValue = "10") Integer size,
                                                        @PathVariable @NotNull Integer courseId,
                                                        @RequestParam(required = false) String groupName) {
    QueryWrapper<CourseGroup> query = Wrappers.query(new CourseGroup().setCourseId(courseId));
    if (courseId != null) {
      query = query.eq(CourseGroup.COL_COURSE_ID, courseId);
    }
    if (StringUtils.isNotBlank(groupName)) {
      query = query.like(CourseGroup.COL_GROUP_NAME, groupName);
    }
    return ResponseEntity.ok(Result.normalOk("Get course-groups page success",
      courseGroupService.page(new Page<>(current, size), query)));
  }

  @ApiOperation(value = "course-group detail",
    notes = "admin users can access all course's group; teacher and student users can only access their own course's group;")
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find course-group with the specific id", response = Result.class)})
  public ResponseEntity<Result<CourseGroup>> detail(@PathVariable @NotNull Integer courseId,
                                                    @PathVariable @NotNull Integer id) {
    final CourseGroup found = courseGroupService.getById(id);
    return found != null ? ResponseEntity.ok(Result.normalOk("Get course-group detail success", found)) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't found course-group with the specific id"));
  }

  @ApiOperation(value = "create course-group",
    notes = "admin users is unrestricted; teacher and student can only create a course-group with own course; student can't create a group when the course's grouping_method is NOT_GROUPING or STRICT_CONTROLLED particularly")
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<Result<CourseGroup>> create(@NotNull @RequestBody @Validated CourseGroup courseGroup) {
    courseGroupService.save(courseGroup);
    return ResponseEntity.created(
      URI.create(String.format("%s/courses/%d/groups/%d", contextPath, courseGroup.getCourseId(), courseGroup.getId())))
      .body(Result.createdOk("Create course-group success, please pay attention to the LOCATION in headers", courseGroup));
  }

  @ApiOperation(value = "update course-group",
    notes = "admin users is unrestricted; teacher and student can only update a course-group with own course; course_id is an immutable field;")
  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<CourseGroup>> update(@PathVariable @NotNull Integer courseId,
                                                    @PathVariable @NotNull Integer id,
                                                    @RequestBody @Validated @NotNull CourseGroup courseGroup) {
    courseGroupService.updateById(courseGroup.setId(id).setCourseId(courseId));
    return ResponseEntity.ok(Result.normalOk("update course-group success", courseGroup));
  }

  @ApiOperation(value = "delete course-group",
    notes = "admin users is unrestricted; teacher and student can only delete a course with own course; student can't delete the group when the course's grouping_method is STRICT_CONTROLLED particularly;all related students will be updated;")
  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @ApiResponses({@ApiResponse(code = 204, message = "Delete course-group success", response = Result.class),
    @ApiResponse(code = 404, message = "Can't find the course-group with the specific id to delete", response = Result.class)})
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer courseId, @PathVariable @NotNull Integer id) {
    return courseGroupService.removeById(id) ? ResponseEntity.status(HttpStatus.NO_CONTENT)
      .body(Result.deleteOk("Delete course-group success")) :
      ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Result.notFound("Can't find the course-group with the specific id to delete"));
  }
}
