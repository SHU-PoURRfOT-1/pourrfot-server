package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.PourrfotTransaction;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.service.PourrfotTransactionService;
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
@RequestMapping("/transactions")
@Slf4j
@Validated
public class PourrfotTransactionController {
  @Value("${server.servlet.contextPath}")
  private String contextPath;
  @Autowired
  private PourrfotTransactionService pourrfotTransactionService;

  @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<PourrfotTransaction>>> page(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                                @RequestParam(required = false, defaultValue = "10") Integer size,
                                                                @RequestParam(required = false) Integer sender,
                                                                @RequestParam(required = false) Integer receiver,
                                                                @RequestParam(required = false) String title,
                                                                @RequestParam(required = false) Boolean isUrgent) {
    QueryWrapper<PourrfotTransaction> query = Wrappers.query(new PourrfotTransaction());
    if (sender != null) {
      query = query.eq(PourrfotTransaction.COL_SENDER, sender);
    }
    if (receiver != null) {
      query = query.eq(PourrfotTransaction.COL_RECEIVER, receiver);
    }
    if (StringUtils.isNotBlank(title)) {
      query = query.like(PourrfotTransaction.COL_TITLE, title);
    }
    if (isUrgent != null) {
      query = query.eq(PourrfotTransaction.COL_URGENT, isUrgent);
    }
    return ResponseEntity.ok(Result.normalOk("Get student-transactions page success",
      pourrfotTransactionService.page(new Page<>(current, size), query)));
  }

  @GetMapping(value = "/detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find student-transactions with the specific id", response = Result.class)})
  public ResponseEntity<Result<PourrfotTransaction>> detail(@PathVariable @NotNull Integer id) {
    final PourrfotTransaction found = pourrfotTransactionService.getById(id);
    return found != null ? ResponseEntity.ok(Result.normalOk("Get student-transactions detail success", found)) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't found student-transactions with the specific id"));
  }

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<PourrfotTransaction>> create(@NotNull @RequestBody @Validated PourrfotTransaction pourrfotTransaction) {
    pourrfotTransactionService.save(pourrfotTransaction);
    return ResponseEntity.ok()
      .location(URI.create(String.format("%s/transactions/detail/%d", contextPath, pourrfotTransaction.getId())))
      .body(Result.createdOk("Create student-transactions success, please pay attention to the LOCATION in headers",
        pourrfotTransaction));
  }

  @PostMapping(value = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<PourrfotTransaction>> update(@PathVariable @NotNull Integer id,
                                                            @RequestBody @Validated @NotNull PourrfotTransaction pourrfotTransaction) {
    pourrfotTransactionService.updateById(pourrfotTransaction.setId(id));
    return ResponseEntity.ok(Result.normalOk("Update student-transactions success", pourrfotTransaction));
  }

  @PostMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 204, message = "Delete student-transactions success", response = Result.class),
    @ApiResponse(code = 404, message = "Can't find the student-transactions with the specific id to delete", response = Result.class)})
  public ResponseEntity<Result<?>> delete(@PathVariable @NotNull Integer id) {
    return pourrfotTransactionService.removeById(id) ? ResponseEntity.ok()
      .body(Result.deleteOk("Delete student-transactions success")) :
      ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Result.notFound("Can't find the student-transactions with the specific id to delete"));
  }
}
