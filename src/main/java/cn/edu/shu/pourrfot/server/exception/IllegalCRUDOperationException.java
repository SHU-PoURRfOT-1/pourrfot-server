package cn.edu.shu.pourrfot.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author spencercjh
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@AllArgsConstructor
@Getter
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Illegal CRUD operation")
public class IllegalCRUDOperationException extends RuntimeException {
  private final String message;
}
