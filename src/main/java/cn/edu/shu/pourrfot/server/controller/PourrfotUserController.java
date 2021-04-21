package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.service.PourrfotUserService;
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
@RequestMapping("/users")
@Slf4j
@Validated
public class PourrfotUserController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private PourrfotUserService pourrfotUserService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<PourrfotUser>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
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
    return ResponseEntity.ok(pourrfotUserService.page(new Page<>(current, size), query));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PourrfotUser> detail(@PathVariable @NotNull Integer id) {
    return ResponseEntity.of(Optional.ofNullable(pourrfotUserService.getById(id)));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<PourrfotUser> create(@NotNull @RequestBody @Validated PourrfotUser pourrfotUser) {
    pourrfotUserService.save(pourrfotUser);
    return ResponseEntity.created(
      URI.create(String.format("%s/users/%d", contextPath, pourrfotUser.getId())))
      .body(pourrfotUser);
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PourrfotUser> update(@PathVariable @NotNull Integer id,
                                             @RequestBody @Validated @NotNull PourrfotUser pourrfotUser) {
    pourrfotUserService.updateById(pourrfotUser.setId(id));
    return ResponseEntity.ok(pourrfotUser);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer id) {
    return pourrfotUserService.removeById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
