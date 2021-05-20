package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.model.dto.SimpleUser;
import cn.edu.shu.pourrfot.server.service.PourrfotUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * @author spencercjh
 */
@RestController
@RequestMapping("/users")
@Slf4j
@Validated
@Api(value = "User CRUD", authorizations = {@Authorization("admin"), @Authorization("teacher"), @Authorization("student")}, tags = "Users")
public class PourrfotUserController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private PourrfotUserService pourrfotUserService;

  @PreAuthorize("hasAnyAuthority('admin','teacher','student')")
  @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<PourrfotUser>>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                         @RequestParam(required = false, defaultValue = "10") Integer size,
                                                         @RequestParam(required = false, value = "role") RoleEnum roleEnum,
                                                         @RequestParam(required = false, value = "sex") SexEnum sexEnum,
                                                         @RequestParam(required = false) String username) {
    QueryWrapper<PourrfotUser> query = Wrappers.query(new PourrfotUser());
    if (roleEnum != null) {
      query = query.eq(PourrfotUser.COL_ROLE, roleEnum);
    }
    if (sexEnum != null) {
      query = query.eq(PourrfotUser.COL_SEX, sexEnum);
    }
    if (StringUtils.isNotBlank(username)) {
      query = query.like(PourrfotUser.COL_USERNAME, username);
    }
    return ResponseEntity.ok(Result.normalOk("Get users page success",
      pourrfotUserService.page(new Page<>(current, size), query)));
  }

  @PreAuthorize("hasAnyAuthority('admin','teacher','student')")
  @GetMapping(value = "/detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find user with the specific id", response = Result.class)})
  public ResponseEntity<Result<PourrfotUser>> detail(@PathVariable @NotNull Integer id) {
    final PourrfotUser found = pourrfotUserService.getById(id);
    return found != null ? ResponseEntity.ok(Result.normalOk("Get user detail success", found)) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't found user with the specific id"));
  }

  @PreAuthorize("hasAnyAuthority('admin','teacher','student')")
  @GetMapping(value = "/detail/current", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find user with the specific id", response = Result.class)})
  public ResponseEntity<Result<PourrfotUser>> current() {
    final SimpleUser user = SimpleUser.of(SecurityContextHolder.getContext().getAuthentication());
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    final PourrfotUser found = pourrfotUserService.getById(user.getId());
    return found != null ? ResponseEntity.ok(Result.normalOk("Get user detail success", found)) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't found user with the specific id"));
  }

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<PourrfotUser>> create(@NotNull @RequestBody @Validated PourrfotUser pourrfotUser) {
    pourrfotUserService.save(pourrfotUser);
    return ResponseEntity.ok()
      .location(URI.create(String.format("%s/users/detail/%d", contextPath, pourrfotUser.getId())))
      .body(Result.createdOk("Create user success, please pay attention to the LOCATION in headers",
        pourrfotUser));
  }

  @PreAuthorize("hasAnyAuthority('admin','teacher','student')")
  @PostMapping(value = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<PourrfotUser>> update(@PathVariable @NotNull Integer id,
                                                     @RequestBody @Validated @NotNull PourrfotUser pourrfotUser) {
    pourrfotUserService.updateById(pourrfotUser.setId(id));
    return ResponseEntity.ok(Result.normalOk("Update user success", pourrfotUser));
  }

  @PostMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 200, message = "Delete user success", response = Result.class),
    @ApiResponse(code = 404, message = "Can't find the user with the specific id to delete", response = Result.class)})
  @PreAuthorize("hasAnyAuthority('admin')")
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer id) {
    return pourrfotUserService.removeById(id) ? ResponseEntity.ok()
      .body(Result.deleteOk("Delete user success")) :
      ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Result.notFound("Can't find the user with the specific id to delete"));
  }
}
