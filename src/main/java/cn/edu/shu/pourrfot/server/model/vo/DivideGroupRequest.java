package cn.edu.shu.pourrfot.server.model.vo;

import cn.edu.shu.pourrfot.server.enums.GroupingMethodEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-vo-DivideGroupRequest")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DivideGroupRequest {
  @ApiModelProperty(required = true, example = "AVERAGE")
  @NotNull
  private GroupingMethodEnum groupingMethod;
  @ApiModelProperty(required = true)
  @Min(0)
  @NotNull
  private Integer expectedGroupSize;
}
