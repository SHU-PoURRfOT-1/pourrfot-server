package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.ProjectMember;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.service.ProjectMemberService;
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
@RequestMapping("/projects/{projectId}/members")
@Slf4j
@Validated
public class ProjectMemberController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private ProjectMemberService projectMemberService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<ProjectMember>>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                          @RequestParam(required = false, defaultValue = "10") Integer size,
                                                          @PathVariable @NotNull Integer projectId,
                                                          @RequestParam(required = false) String roleName) {
    QueryWrapper<ProjectMember> query = Wrappers.query(new ProjectMember());
    if (StringUtils.isNotBlank(roleName)) {
      query = query.eq(ProjectMember.COL_ROLE_NAME, roleName);
    }
    if (projectId != null) {
      query = query.eq(ProjectMember.COL_PROJECT_ID, projectId);
    }
    return ResponseEntity.ok(Result.normalOk("Get project-members page success",
      projectMemberService.page(new Page<>(current, size), query)));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find project-member with the specific id", response = Result.class)})
  public ResponseEntity<Result<ProjectMember>> detail(@PathVariable @NotNull Integer projectId,
                                                      @PathVariable @NotNull Integer id) {
    final ProjectMember found = projectMemberService.getById(id);
    return found != null ? ResponseEntity.ok(Result.normalOk("Get project-member detail success", found)) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't found project-member with the specific id"));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<Result<ProjectMember>> create(@PathVariable @NotNull Integer projectId,
                                                      @NotNull @RequestBody @Validated ProjectMember projectMember) {
    projectMemberService.save(projectMember.setProjectId(projectId));
    return ResponseEntity.created(
      URI.create(String.format("%s/projects/%d/members/%d", contextPath, projectMember.getProjectId(), projectMember.getId())))
      .body(Result.createdOk("Create project-member success, please pay attention to the LOCATION in headers", projectMember));
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<ProjectMember>> update(@PathVariable @NotNull Integer projectId,
                                                      @PathVariable @NotNull Integer id,
                                                      @RequestBody @Validated @NotNull ProjectMember projectMember) {
    projectMemberService.updateById(projectMember.setProjectId(projectId).setId(id));
    return ResponseEntity.ok(Result.normalOk("Update project-member success", projectMember));
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @ApiResponses({@ApiResponse(code = 204, message = "Delete project-member success", response = Result.class),
    @ApiResponse(code = 404, message = "Can't find the project-member with the specific id to delete", response = Result.class)})
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer projectId,
                                  @PathVariable @NotNull Integer id) {
    return projectMemberService.removeById(id) ? ResponseEntity.status(HttpStatus.NO_CONTENT)
      .body(Result.deleteOk("Delete project-member success")) :
      ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Result.notFound("Can't find the project-member with the specific id to delete"));
  }
}
