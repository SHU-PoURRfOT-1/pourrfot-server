package cn.edu.shu.pourrfot.server.controller;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
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
  @ApiResponses({@ApiResponse(code = 400, message = "test bad request")})
  public ResponseEntity<String> helloWithName(@PathVariable String name) {
    return ResponseEntity.badRequest().body(name);
  }
}
