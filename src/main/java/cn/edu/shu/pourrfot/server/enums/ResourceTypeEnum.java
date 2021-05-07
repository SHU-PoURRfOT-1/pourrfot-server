package cn.edu.shu.pourrfot.server.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author spencercjh
 */
@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum ResourceTypeEnum implements IEnum<String> {
  courses("courses"),
  projects("projects"),
  groups("groups"),
  transactions("transactions"),
  messages("messages"),
  unknown("unknown");
  private final String name;

  ResourceTypeEnum(String name) {
    this.name = name;
  }

  @Override
  public String getValue() {
    return name;
  }
}
