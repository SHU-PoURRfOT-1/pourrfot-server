package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.service.ProjectService;
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
@RestController
@RequestMapping("/projects")
@Slf4j
@Validated
@Api(value = "Projects CRUD", authorizations = {@Authorization("admin"), @Authorization("teacher")}, tags = "Projects")
public class ProjectController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private ProjectService projectService;

  @ApiOperation(value = "projects page",
    notes = "admin users can access all projects;\n" +
      "teacher and student users can only access their own projects.")
  @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<Project>>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                    @RequestParam(required = false, defaultValue = "10") Integer size,
                                                    @RequestParam(required = false) String projectName,
                                                    @RequestParam(required = false) String projectCode,
                                                    @RequestParam(required = false) Integer ownerId) {
    QueryWrapper<Project> query = Wrappers.query(new Project());
    if (StringUtils.isNotBlank(projectName)) {
      query = query.like(Project.COL_PROJECT_NAME, projectName);
    }
    if (StringUtils.isNotBlank(projectCode)) {
      query = query.like(Project.COL_PROJECT_CODE, projectCode);
    }
    if (ownerId != null) {
      query = query.eq(Project.COL_OWNER_ID, ownerId);
    }
    return ResponseEntity.ok(Result.normalOk("Get projects page success",
      projectService.page(new Page<>(current, size), query)));
  }

  @ApiOperation(value = "project detail",
    notes = "admin users can access all projects;\n" +
      "teacher and student users can only access their own projects.")
  @GetMapping(value = "/detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find project with the specific id", response = Result.class)})
  public ResponseEntity<Result<Project>> detail(@PathVariable @NotNull Integer id) {
    final Project found = projectService.getById(id);
    return found != null ? ResponseEntity.ok(Result.normalOk("Get project detail success", found)) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't found project with the specific id"));
  }

  @ApiOperation(value = "create project",
    notes = "admin users is unrestricted but can't create a project with student owner;\n" +
      "only teacher user can create a project with own id.")
  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<Result<Project>> create(@NotNull @RequestBody @Validated Project project) {
    projectService.save(project);
    return ResponseEntity.created(
      URI.create(String.format("%s/projects/detail/%d", contextPath, project.getId())))
      .body(Result.createdOk("Create project success, please pay attention to the LOCATION in headers", project));
  }

  @ApiOperation(value = "update project",
    notes = "admin users is unrestricted but can't update a project with student owner;\n" +
      "only project owner teacher can update project with own id.")
  @PostMapping(value = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Project>> update(@PathVariable @NotNull Integer id,
                                                @RequestBody @Validated @NotNull Project project) {
    projectService.updateById(project.setId(id));
    return ResponseEntity.ok(Result.normalOk("Update project success", project));
  }

  @ApiOperation(value = "update project",
    notes = "admin users is unrestricted;\n" +
      "only project owner teacher can update project.")
  @PostMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @ApiResponses({@ApiResponse(code = 204, message = "Delete project success", response = Result.class),
    @ApiResponse(code = 404, message = "Can't find the project with the specific id to delete", response = Result.class)})
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer id) {
    return projectService.removeById(id) ? ResponseEntity.status(HttpStatus.NO_CONTENT)
      .body(Result.deleteOk("Delete project success")) :
      ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Result.notFound("Can't find the project with the specific id to delete"));
  }
}
