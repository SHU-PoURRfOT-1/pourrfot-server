package cn.edu.shu.pourrfot.server.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author spencercjh
 */
@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum GroupingMethodEnum implements IEnum<String> {
  NOT_GROUPING("NOT_GROUPING"),
  FREE("FREE"),
  AVERAGE("AVERAGE"),
  STRICT_CONTROLLED("STRICT_CONTROLLED");

  private final String value;

  GroupingMethodEnum(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }
}
