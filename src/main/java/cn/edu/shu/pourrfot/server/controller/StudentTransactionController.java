package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.StudentTransaction;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.service.StudentTransactionService;
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
@RequestMapping("/student-transactions")
@Slf4j
@Validated
public class StudentTransactionController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private StudentTransactionService studentTransactionService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<StudentTransaction>>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
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
    return ResponseEntity.ok(Result.normalOk("Get student-transactions page success",
      studentTransactionService.page(new Page<>(current, size), query)));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find student-transactions with the specific id", response = Result.class)})
  public ResponseEntity<Result<StudentTransaction>> detail(@PathVariable @NotNull Integer id) {
    final StudentTransaction found = studentTransactionService.getById(id);
    return found != null ? ResponseEntity.ok(Result.normalOk("Get student-transactions detail success", found)) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't found student-transactions with the specific id"));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<Result<StudentTransaction>> create(@NotNull @RequestBody @Validated StudentTransaction studentTransaction) {
    studentTransactionService.save(studentTransaction);
    return ResponseEntity.created(
      URI.create(String.format("%s/student-transactions/%d", contextPath, studentTransaction.getId())))
      .body(Result.createdOk("Create student-transactions success, please pay attention to the LOCATION in headers", studentTransaction));
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<StudentTransaction>> update(@PathVariable @NotNull Integer id,
                                                           @RequestBody @Validated @NotNull StudentTransaction studentTransaction) {
    studentTransactionService.updateById(studentTransaction.setId(id));
    return ResponseEntity.ok(Result.normalOk("Update student-transactions success", studentTransaction));
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @ApiResponses({@ApiResponse(code = 204, message = "Delete student-transactions success", response = Result.class),
    @ApiResponse(code = 404, message = "Can't find the student-transactions with the specific id to delete", response = Result.class)})
  public ResponseEntity<Result<?>> delete(@PathVariable @NotNull Integer id) {
    return studentTransactionService.removeById(id) ? ResponseEntity.status(HttpStatus.NO_CONTENT)
      .body(Result.deleteOk("Delete student-transactions success")) :
      ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Result.notFound("Can't find the student-transactions with the specific id to delete"));
  }
}
