package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.ProjectUser;
import cn.edu.shu.pourrfot.server.service.ProjectUserService;
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
  public ResponseEntity<Page<ProjectUser>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
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
    return ResponseEntity.ok(projectUserService.page(new Page<>(current, size), query));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProjectUser> detail(@PathVariable @NotNull Integer projectId,
                                            @PathVariable @NotNull Integer id) {
    return ResponseEntity.of(Optional.ofNullable(projectUserService.getById(id)));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<ProjectUser> create(@PathVariable @NotNull Integer projectId,
                                            @NotNull @RequestBody @Validated ProjectUser projectUser) {
    projectUserService.save(projectUser.setProjectId(projectId));
    return ResponseEntity.created(
      URI.create(String.format("%s/projects/%d/users/%d", contextPath, projectUser.getProjectId(), projectUser.getId())))
      .body(projectUser);
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProjectUser> update(@PathVariable @NotNull Integer projectId,
                                            @PathVariable @NotNull Integer id,
                                            @RequestBody @Validated @NotNull ProjectUser projectUser) {
    projectUserService.updateById(projectUser.setProjectId(projectId).setId(id));
    return ResponseEntity.ok(projectUser);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer projectId,
                                  @PathVariable @NotNull Integer id) {
    return projectUserService.removeById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
