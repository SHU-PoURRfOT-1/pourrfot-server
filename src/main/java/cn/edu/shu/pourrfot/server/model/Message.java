package cn.edu.shu.pourrfot.server.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
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
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-Message")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "message")
@Accessors(chain = true)
public class Message {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_PARENT_MESSAGE_ID = "parent_message_id";
  public static final String COL_SENDER = "sender";
  public static final String COL_RECEIVER = "receiver";
  public static final String COL_TITLE = "title";
  public static final String COL_URGENT = "urgent";
  public static final String COL_REGULAR = "regular";
  public static final String COL_CONTENT = "content";
  public static final String COL_METADATA = "metadata";
  @TableId(value = "id", type = IdType.AUTO)
  @ApiModelProperty()
  private Integer id;
  @TableField(value = "create_time")
  @ApiModelProperty()
  private Date createTime;
  @TableField(value = "update_time")
  @ApiModelProperty()
  @Version
  private Date updateTime;
  /**
   * reply to a (parent) message. 0 represents nothing
   */
  @TableField(value = "parent_message_id")
  @ApiModelProperty(value = "reply to a (parent) message. 0 represents nothing", example = "0")
  private Integer parentMessageId;
  @TableField(value = "sender")
  @ApiModelProperty(required = true)
  @NotNull
  private Integer sender;
  @TableField(value = "receiver")
  @ApiModelProperty(required = true)
  @NotNull
  private Integer receiver;
  @TableField(value = "title")
  @ApiModelProperty(required = true)
  @NotBlank
  private String title;
  @TableField(value = "urgent")
  @ApiModelProperty(example = "false")
  private Boolean urgent;
  @TableField(value = "regular")
  @ApiModelProperty(example = "false")
  private Boolean regular;
  @TableField(value = "content")
  @ApiModelProperty(required = true)
  @NotBlank
  private String content;
  @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
  @ApiModelProperty()
  private Map<String, Object> metadata;
}
