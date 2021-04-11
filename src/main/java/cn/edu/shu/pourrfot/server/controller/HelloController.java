package cn.edu.shu.pourrfot.server.controller;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author spencercjh
 */
@RestController
@RequestMapping("/hello")
public class HelloController {
  @GetMapping("/{name}")
  @ApiResponses({@ApiResponse(code = 400, message = "name")})
  public ResponseEntity<String> hello(@PathVariable(required = false) String name) {
    return ResponseEntity.badRequest().body(StringUtils.hasText(name) ? name : "empty");
  }
}
