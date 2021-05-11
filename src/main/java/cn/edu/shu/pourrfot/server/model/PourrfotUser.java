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
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@Accessors(chain = true)
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
  public static final String COL_EMAIL = "email";
  public static final String COL_TELEPHONE = "telephone";
  public static final String COL_PASSWORD = "password";

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
  @TableField(value = "username")
  @ApiModelProperty(required = true)
  @NotBlank
  private String username;
  @TableField(value = "nickname")
  @ApiModelProperty(required = true)
  @NotBlank
  private String nickname;
  @TableField(value = "profile_photo")
  @ApiModelProperty()
  private String profilePhoto;
  @TableField(value = "birth")
  @ApiModelProperty()
  private Date birth;
  @TableField(value = "sex")
  @ApiModelProperty(required = true)
  @NotNull
  private SexEnum sex;
  @TableField(value = "`role`")
  @ApiModelProperty(required = true)
  @NotNull
  private RoleEnum role;
  @TableField(value = "email")
  @ApiModelProperty
  private String email;
  @TableField(value = "telephone")
  @ApiModelProperty
  private String telephone;
  @TableField(value = "`password`")
  @ApiModelProperty
  private String password;
}
