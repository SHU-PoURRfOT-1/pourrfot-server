package cn.edu.shu.pourrfot.server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author spencercjh
 */
@RestController
@RequestMapping
public class HelloController {
  @GetMapping
  public ResponseEntity<String> hello() {
    return ResponseEntity.badRequest().body("123");
  }
}
