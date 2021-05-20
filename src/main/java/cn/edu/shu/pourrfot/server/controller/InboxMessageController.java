package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.model.dto.SingleMessage;
import cn.edu.shu.pourrfot.server.service.InboxMessageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * NOT SUPPORT UPDATE AND ONlY SUPPORT DELETE
 *
 * @author spencercjh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/inbox-messages")
public class InboxMessageController {
  @Autowired
  private InboxMessageService inboxMessageService;

  @PreAuthorize("hasAnyAuthority('admin','teacher','student')")
  @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<SingleMessage>>> list(@RequestParam(required = false, defaultValue = "1") @Min(1)
                                                            Integer current,
                                                          @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(1000)
                                                            Integer size,
                                                          @RequestParam(required = false) Integer sender,
                                                          @RequestParam(required = false) Integer receiver,
                                                          @RequestParam(required = false) String title,
                                                          @RequestParam(required = false) Boolean isUrgent,
                                                          @RequestParam(required = false) Boolean isRegular,
                                                          @RequestParam(required = false) Boolean haveRead) {

    return ResponseEntity.ok(Result.normalOk("Get messages page success",
      inboxMessageService.messagePage(sender, receiver, title, isUrgent, isRegular, haveRead, current, size)));
  }

  @PreAuthorize("hasAnyAuthority('admin','teacher','student')")
  @GetMapping(value = "/detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find message with the specific id", response = Result.class)})
  public ResponseEntity<Result<SingleMessage>> detail(@PathVariable Integer id) {
    final SingleMessage found = inboxMessageService.getMessageByInboxMessageId(id);
    return found != null ? ResponseEntity.ok(Result.normalOk("Get message detail success", found)) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't found message with the specific id"));
  }
}
