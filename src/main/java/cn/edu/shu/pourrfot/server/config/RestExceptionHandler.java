package cn.edu.shu.pourrfot.server.config;

import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.exception.OssFileServiceException;
import cn.edu.shu.pourrfot.server.model.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author spencercjh
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                 HttpStatus status, WebRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound(StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString()));
  }

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
  protected ResponseEntity<Result<?>> duplicateKeyException(DuplicateKeyException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
      .body(Result.of(HttpStatus.CONFLICT,
        "Some elements that need to be globally unique are duplicated, causing the creation of resources to fail",
        null));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                HttpStatus status, WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                           HttpStatus status, WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                       HttpHeaders headers, HttpStatus status,
                                                                       WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                   HttpHeaders headers, HttpStatus status,
                                                                   WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                                    HttpHeaders headers, HttpStatus status,
                                                                    WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                        HttpHeaders headers, HttpStatus status,
                                                                        WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                        HttpHeaders headers, HttpStatus status,
                                                                        WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers,
                                                                HttpStatus status, WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status,
                                                      WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                                HttpStatus status, WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers,
                                                                HttpStatus status, WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
                                                                   HttpHeaders headers, HttpStatus status,
                                                                   WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
                                                       WebRequest request) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }

  @Override
  protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
                                                                      HttpHeaders headers, HttpStatus status,
                                                                      WebRequest webRequest) {
    return ResponseEntity.status(status).body(Result.of(status, StringUtils.isNotBlank(ex.getMessage()) ?
      ex.getMessage() : ex.toString(), null));
  }
}
