package cn.edu.shu.pourrfot.server.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.util.List;

/**
 * @author spencercjh
 */

@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum RoleEnum implements IEnum<String> {
  student("student"),
  teacher("teacher"),
  admin("admin"),
  guest("guest");

  public static List<String> ALL_ROLE_VALUES = List.of(student.role, teacher.role, admin.role);
  private final String role;

  RoleEnum(String role) {
    this.role = role;
  }

  @Override
  public String getValue() {
    return role;
  }
}
