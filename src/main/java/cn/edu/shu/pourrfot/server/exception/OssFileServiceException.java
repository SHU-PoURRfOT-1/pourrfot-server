package cn.edu.shu.pourrfot.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author specencercjh
 */
@Getter
@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "File service is temporarily unavailable")
public class OssFileServiceException extends RuntimeException {
  public OssFileServiceException(Throwable cause) {
    super(cause);
  }
}
