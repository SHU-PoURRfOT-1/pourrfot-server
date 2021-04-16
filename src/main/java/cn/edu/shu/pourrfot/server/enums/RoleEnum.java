package cn.edu.shu.pourrfot.server.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author spencercjh
 */

@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum RoleEnum implements IEnum<String> {
  student("student"),
  teacher("teacher"),
  admin("admin"),
  guest("guest");

  private final String role;


  RoleEnum(String role) {
    this.role = role;
  }

  @Override
  public String getValue() {
    return role;
  }
}
