package cn.edu.shu.pourrfot.server.model;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author spencercjh
 */
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-PourrfotUser")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pourrfot_user")
public class PourrfotUser {
  public static final String COL_ID = "id";
  public static final String COL_CREATE_TIME = "create_time";
  public static final String COL_UPDATE_TIME = "update_time";
  public static final String COL_USERNAME = "username";
  public static final String COL_NICKNAME = "nickname";
  public static final String COL_PROFILE_PHOTO = "profile_photo";
  public static final String COL_BIRTH = "birth";
  public static final String COL_SEX = "sex";
  public static final String COL_ROLE = "role";
  @TableId(value = "id", type = IdType.AUTO)
  @ApiModelProperty(value = "")
  private Integer id;
  @TableField(value = "create_time")
  @ApiModelProperty(value = "")
  private Date createTime;
  @TableField(value = "update_time")
  @ApiModelProperty(value = "")
  @Version
  private Date updateTime;
  @TableField(value = "username")
  @ApiModelProperty(value = "")
  private String username;
  @TableField(value = "nickname")
  @ApiModelProperty(value = "")
  private String nickname;
  @TableField(value = "profile_photo")
  @ApiModelProperty(value = "")
  private String profilePhoto;
  @TableField(value = "birth")
  @ApiModelProperty(value = "")
  private Date birth;
  @TableField(value = "sex")
  @ApiModelProperty(value = "")
  private SexEnum sex;
  @TableField(value = "`role`")
  @ApiModelProperty(value = "")
  private RoleEnum role;
}
