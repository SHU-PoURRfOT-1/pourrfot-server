package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.service.ProjectService;
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
@RequestMapping("/projects")
@Slf4j
@Validated
public class ProjectController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private ProjectService projectService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<Project>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
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
    return ResponseEntity.ok(projectService.page(new Page<>(current, size), query));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Project> detail(@PathVariable @NotNull Integer id) {
    return ResponseEntity.of(Optional.ofNullable(projectService.getById(id)));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<Project> create(@NotNull @RequestBody @Validated Project project) {
    projectService.save(project);
    return ResponseEntity.created(
      URI.create(String.format("%s/projects/%d", contextPath, project.getId())))
      .body(project);
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Project> update(@PathVariable @NotNull Integer id,
                                        @RequestBody @Validated @NotNull Project project) {
    projectService.updateById(project.setId(id));
    return ResponseEntity.ok(project);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer id) {
    return projectService.removeById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
