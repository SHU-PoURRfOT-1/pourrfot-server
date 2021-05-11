package cn.edu.shu.pourrfot.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author spencercjh
 */
@AllArgsConstructor
@Getter
@ResponseStatus(code = HttpStatus.PRECONDITION_REQUIRED, reason = "Can't operate because not found the resource")
public class NotFoundException extends RuntimeException {
  private final String message;
}
