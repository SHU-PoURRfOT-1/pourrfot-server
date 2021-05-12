package cn.edu.shu.pourrfot.server.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-InboxMessage")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "inbox_message")
@Accessors(chain = true)
public class InboxMessage {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_SENDER = "sender";
  public static final String COL_RECEIVER = "receiver";
  public static final String COL_MESSAGE_ID = "message_id";
  public static final String COL_HAVE_READ = "have_read";
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
  @TableField(value = "sender")
  @ApiModelProperty(required = true)
  @NotNull
  private Integer sender;
  @TableField(value = "receiver")
  @ApiModelProperty(required = true)
  @NotNull
  private Integer receiver;
  @TableField(value = "message_id")
  @ApiModelProperty(required = true)
  @NotNull
  private Integer messageId;
  @TableField(value = "have_read")
  @ApiModelProperty(example = "false")
  private Boolean haveRead;
}
