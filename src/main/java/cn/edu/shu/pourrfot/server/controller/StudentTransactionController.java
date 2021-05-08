package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.StudentTransaction;
import cn.edu.shu.pourrfot.server.service.StudentTransactionService;
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
@RequestMapping("/student-transactions")
@Slf4j
@Validated
public class StudentTransactionController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private StudentTransactionService studentTransactionService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<StudentTransaction>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                       @RequestParam(required = false, defaultValue = "10") Integer size,
                                                       @RequestParam(required = false) Integer sender,
                                                       @RequestParam(required = false) Integer receiver,
                                                       @RequestParam(required = false) String title,
                                                       @RequestParam(required = false) Boolean isUrgent) {
    QueryWrapper<StudentTransaction> query = Wrappers.query(new StudentTransaction());
    if (sender != null) {
      query = query.eq(StudentTransaction.COL_SENDER, sender);
    }
    if (receiver != null) {
      query = query.eq(StudentTransaction.COL_RECEIVER, receiver);
    }
    if (StringUtils.isNotBlank(title)) {
      query = query.like(StudentTransaction.COL_TITLE, title);
    }
    if (isUrgent != null) {
      query = query.eq(StudentTransaction.COL_URGENT, isUrgent);
    }
    return ResponseEntity.ok(studentTransactionService.page(new Page<>(current, size), query));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StudentTransaction> detail(@PathVariable @NotNull Integer id) {
    return ResponseEntity.of(Optional.ofNullable(studentTransactionService.getById(id)));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<StudentTransaction> create(@NotNull @RequestBody @Validated StudentTransaction studentTransaction) {
    studentTransactionService.save(studentTransaction);
    return ResponseEntity.created(
      URI.create(String.format("%s/student-transactions/%d", contextPath, studentTransaction.getId())))
      .body(studentTransaction);
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StudentTransaction> update(@PathVariable @NotNull Integer id,
                                                   @RequestBody @Validated @NotNull StudentTransaction studentTransaction) {
    studentTransactionService.updateById(studentTransaction.setId(id));
    return ResponseEntity.ok(studentTransaction);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public ResponseEntity<?> delete(@PathVariable @NotNull Integer id) {
    return studentTransactionService.removeById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
