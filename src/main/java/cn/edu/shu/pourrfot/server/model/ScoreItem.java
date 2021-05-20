package cn.edu.shu.pourrfot.server.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-ScoreItem")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScoreItem {
  @ApiModelProperty(required = true, notes = "English")
  @NotBlank
  private String name;
  @ApiModelProperty(required = true)
  @NotNull
  @Max(100)
  @Min(0)
  private Double score;
  @ApiModelProperty(required = true)
  @NotNull
  @Min(0)
  @Max(1)
  private Double weight;
  @ApiModelProperty(required = true, notes = "Usual Chinese")
  @NotBlank
  private String description;
}
