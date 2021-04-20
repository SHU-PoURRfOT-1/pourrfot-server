package cn.edu.shu.pourrfot.server.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-ScoreItem")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScoreItem {
  private Double score;
  private Double weight;
  private String description;
}
