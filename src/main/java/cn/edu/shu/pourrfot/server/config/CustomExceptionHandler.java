package cn.edu.shu.pourrfot.server.config;

import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.exception.OssFileServiceException;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author spencercjh
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
  @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
  @ExceptionHandler({IllegalCRUDOperationException.class, IllegalArgumentException.class})
  protected ResponseEntity<Result<?>> handleIllegalCRUDOperationException(IllegalCRUDOperationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.badRequest(StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString()));
  }

  @ExceptionHandler(NotFoundException.class)
  protected ResponseEntity<Result<?>> handleResourceNotFoundException(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(Result.notFound(StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString()));
  }

  @ExceptionHandler(OssFileServiceException.class)
  protected ResponseEntity<Result<?>> handleOssFileServiceException(OssFileServiceException ex) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
      .body(Result.of(HttpStatus.SERVICE_UNAVAILABLE, StringUtils.isNotBlank(ex.getMessage()) ?
        ex.getMessage() : ex.toString(), null));
  }

  @ExceptionHandler(DuplicateKeyException.class)
  protected ResponseEntity<Result<?>> handleDuplicateKeyException(DuplicateKeyException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
      .body(Result.of(HttpStatus.CONFLICT,
        "Some elements that need to be globally unique are duplicated, causing the creation of resources to fail",
        null));
  }
}
