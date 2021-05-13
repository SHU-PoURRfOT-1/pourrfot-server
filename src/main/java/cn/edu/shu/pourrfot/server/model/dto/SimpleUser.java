package cn.edu.shu.pourrfot.server.model.dto;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.Authentication;

import java.util.Map;

/**
 * @author spencercjh
 */
@Getter
@EqualsAndHashCode
@ToString
public class SimpleUser {
  private final int id;
  private final String username;
  private final RoleEnum role;

  private SimpleUser(int id, String username, RoleEnum role) {
    this.id = id;
    this.username = username;
    this.role = role;
  }

  @SuppressWarnings("unchecked")
  public static SimpleUser of(Authentication authentication) {
    // in local test or unit test, there is no authentication
    if (authentication == null) {
      return null;
    }
    final Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
    if (details == null) {
      return null;
    }
    return new SimpleUser(Integer.parseInt(details.get("id").toString()),
      (String) details.get("username"),
      RoleEnum.valueOf((String) details.get("role")));
  }
}
