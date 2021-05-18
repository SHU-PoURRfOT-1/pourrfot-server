package cn.edu.shu.pourrfot.server.controller;

import cn.edu.shu.pourrfot.server.model.Message;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import cn.edu.shu.pourrfot.server.service.MessageService;
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
 * NOT SUPPORT UPDATE AND ONlY SUPPORT DELETE
 *
 * @author spencercjh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/messages")
public class MessageController {
  @Autowired
  private MessageService messageService;
  @Value("${server.servlet.contextPath}")
  private String contextPath;

  @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Result<Page<Message>>> list(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                    @RequestParam(required = false, defaultValue = "10") Integer size,
                                                    @RequestParam(required = false) Integer sender,
                                                    @RequestParam(required = false) Integer receiver,
                                                    @RequestParam(required = false) String title,
                                                    @RequestParam(required = false) Boolean isUrgent,
                                                    @RequestParam(required = false) Boolean isRegular) {
    QueryWrapper<Message> query = Wrappers.query(new Message());
    if (sender != null) {
      query = query.eq(Message.COL_SENDER, sender);
    }
    if (receiver != null) {
      query = query.eq(Message.COL_RECEIVER, receiver);
    }
    if (StringUtils.isNotBlank(title)) {
      query = query.like(Message.COL_TITLE, title.trim());
    }
    if (isUrgent != null) {
      query = query.eq(Message.COL_URGENT, isUrgent);
    }
    if (isRegular != null) {
      query = query.eq(Message.COL_REGULAR, isRegular);
    }
    return ResponseEntity.ok(Result.normalOk("Get messages page success",
      messageService.page(new Page<>(current, size), query)));
  }

  @GetMapping(value = "/detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({@ApiResponse(code = 404, message = "Can't find message with the specific id", response = Result.class)})
  public ResponseEntity<Result<Message>> detail(@PathVariable Integer id) {
    final Message found = messageService.getById(id);
    return found != null ? ResponseEntity.ok(Result.normalOk("Get message detail success", found)) :
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound("Can't found message with the specific id"));
  }

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public ResponseEntity<Result<Message>> create(@NotNull @RequestBody @Validated Message message) {
    messageService.save(message);
    return ResponseEntity.created(URI.create(String.format("%s/messages/detail/%d", contextPath, message.getId())))
      .body(Result.createdOk("Create message success, please pay attention to the LOCATION in headers", message));
  }

  @PostMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @ApiResponses({@ApiResponse(code = 204, message = "Delete message success", response = Result.class),
    @ApiResponse(code = 404, message = "Can't find the message with the specific id to delete", response = Result.class)})
  public ResponseEntity<Result<?>> delete(@PathVariable @NotNull Integer id) {
    return messageService.removeById(id) ? ResponseEntity.status(HttpStatus.NO_CONTENT)
      .body(Result.deleteOk("Delete message success")) :
      ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Result.notFound("Can't find the message with the specific id to delete"));
  }
}
