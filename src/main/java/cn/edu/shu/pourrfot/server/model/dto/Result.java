package cn.edu.shu.pourrfot.server.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-dto-Result")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Result<T> {
  private int code;
  private T data;
  private String message;

  public static <A> Result<A> createdOk(String message, A data) {
    return new Result<>(HttpStatus.CREATED.value(), data, message);
  }

  public static <A> Result<A> deleteOk(String message) {
    return (Result<A>) Result.builder().code(HttpStatus.NO_CONTENT.value()).message(message).build();
  }

  public static <A> Result<A> normalOk(String message, A data) {
    return new Result<>(HttpStatus.OK.value(), data, message);
  }

  public static <A> Result<A> notFound(String message) {
    return (Result<A>) Result.builder().code(HttpStatus.NOT_FOUND.value()).message(message).build();
  }

  public static <A> Result<A> badRequest(String message) {
    return (Result<A>) Result.builder().code(HttpStatus.BAD_REQUEST.value()).message(message).build();
  }

  public static <A> Result<A> of(HttpStatus httpStatus, String message, A data) {
    return new Result<>(httpStatus.value(), data, message);
  }

  public static <A> Result<A> of(HttpStatus httpStatus, String message) {
    return new Result<>(httpStatus.value(), null, message);
  }
}
