package cn.edu.shu.pourrfot.server.filter;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author spencercjh
 */
public class SimpleGrantedAuthority implements GrantedAuthority {
  private final RoleEnum role;

  public SimpleGrantedAuthority(RoleEnum role) {
    this.role = role;
  }

  @Override
  public String getAuthority() {
    return role.getValue();
  }
}
