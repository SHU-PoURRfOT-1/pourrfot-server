package cn.edu.shu.pourrfot.server.model.dto;

import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-dto-SingleMessage")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SingleMessage {
  @ApiModelProperty(value = "Inbox message id")
  private Integer id;
  @ApiModelProperty(value = "Inbox message create time")
  private Date createTime;
  @ApiModelProperty(value = "Inbox message update time")
  @Version
  private Date updateTime;
  /**
   * reply to a (parent) message. 0 represents nothing
   */
  @ApiModelProperty(value = "reply to a (parent) message. 0 represents nothing", example = "0")
  private Integer parentMessageId;
  @ApiModelProperty(required = true)
  @NotNull
  private Integer sender;
  @ApiModelProperty(required = true)
  @NotNull
  private Integer receiver;
  @ApiModelProperty(required = true)
  @NotBlank
  private String title;
  @ApiModelProperty(example = "false")
  private Boolean urgent;
  @ApiModelProperty(example = "false")
  private Boolean regular;
  @ApiModelProperty(required = true)
  @NotBlank
  private String content;
  @ApiModelProperty()
  private Map<String, Object> metadata;
  @ApiModelProperty(example = "false")
  private Boolean haveRead;
}
