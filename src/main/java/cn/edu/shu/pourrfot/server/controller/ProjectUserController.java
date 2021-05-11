package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.ProjectUser;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.service.ProjectUserService;
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
@RestController
@RequestMapping("/projects/{projectId}/users")
@Slf4j
@Validated
public class ProjectUserController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private ProjectUserService projectUserService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<ProjectUser>>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                        @RequestParam(required = false, defaultValue = "10") Integer size,
                                                        @PathVariable @NotNull Integer projectId,
                                                        @RequestParam(required = false) String roleName) {
    QueryWrapper<ProjectUser> query = Wrappers.query(new ProjectUser());
    if (StringUtils.isNotBlank(roleName)) {
      query = query.eq(ProjectUser.COL_ROLE_NAME, roleName);
    }
    if (projectId != null) {
      query = query.eq(ProjectUser.COL_PROJECT_ID, projectId);
    }
    return ResponseEntity.ok(Result.normalOk("Get project-users page success",
      projectUserService.page(new Page<>(current, size), query)));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find project-user with the specific id", response = Result.class)})
  public ResponseEntity<Result<ProjectUser>> detail(@PathVariable @NotNull Integer projectId,
                                                    @PathVariable @NotNull Integer id) {
    final ProjectUser found = projectUserService.getById(id);
    return found != null ? ResponseEntity.ok(Result.normalOk("Get project-user detail success", found)) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't found project-user with the specific id"));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<Result<ProjectUser>> create(@PathVariable @NotNull Integer projectId,
                                                    @NotNull @RequestBody @Validated ProjectUser projectUser) {
    projectUserService.save(projectUser.setProjectId(projectId));
    return ResponseEntity.created(
      URI.create(String.format("%s/projects/%d/users/%d", contextPath, projectUser.getProjectId(), projectUser.getId())))
      .body(Result.createdOk("Create project-user success, please pay attention to the LOCATION in headers", projectUser));
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<ProjectUser>> update(@PathVariable @NotNull Integer projectId,
                                                    @PathVariable @NotNull Integer id,
                                                    @RequestBody @Validated @NotNull ProjectUser projectUser) {
    projectUserService.updateById(projectUser.setProjectId(projectId).setId(id));
    return ResponseEntity.ok(Result.normalOk("Update project-user success", projectUser));
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @ApiResponses({@ApiResponse(code = 204, message = "Delete project-user success", response = Result.class),
    @ApiResponse(code = 404, message = "Can't find the project-user with the specific id to delete", response = Result.class)})
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer projectId,
                                  @PathVariable @NotNull Integer id) {
    return projectUserService.removeById(id) ? ResponseEntity.status(HttpStatus.NO_CONTENT)
      .body(Result.deleteOk("Delete project-user success")) :
      ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Result.notFound("Can't find the project-user with the specific id to delete"));
  }
}
