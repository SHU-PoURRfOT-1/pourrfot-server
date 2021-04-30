package cn.edu.shu.pourrfot.server.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.util.List;

/**
 * @author spencercjh
 */
@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum ResourceTypeEnum implements IEnum<String> {
  courses("courses"),
  projects("projects"),
  transactions("transactions"),
  messages("messages"),
  unknown("unknown");
  public static List<String> ALL_ROLE_VALUES = List.of(courses.name, projects.name, transactions.name, messages.name, unknown.name);
  public static List<ResourceTypeEnum> ALL_ROLE = List.of(courses, projects, transactions, messages, unknown);
  private final String name;

  ResourceTypeEnum(String name) {
    this.name = name;
  }

  @Override
  public String getValue() {
    return name;
  }
}
