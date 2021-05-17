package cn.edu.shu.pourrfot.server.model.dto;

import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.enums.SexEnum;
import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel(value = "cn-edu-shu-pourrfot-server-model-dto-ProjectMemberUser")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ProjectMemberUser {
  @ApiModelProperty()
  @NotNull
  private Integer id;
  @ApiModelProperty()
  @NotNull
  private Integer userId;
  @ApiModelProperty()
  @NotNull
  private Integer projectId;
  @ApiModelProperty()
  private Date createTime;
  @ApiModelProperty()
  private Date updateTime;
  /**
   * role in the project
   */
  @ApiModelProperty(value = "role in the project")
  @NotBlank
  private String projectRole;
  @ApiModelProperty()
  @NotBlank
  private String username;
  @ApiModelProperty()
  @NotBlank
  private String nickname;
  @TableField(value = "profile_photo")
  @ApiModelProperty()
  private String profilePhoto;
  @ApiModelProperty()
  private Date birth;
  @ApiModelProperty()
  @NotNull
  private SexEnum sex;
  @ApiModelProperty()
  @NotNull
  private RoleEnum role;
  @ApiModelProperty
  private String email;
  @ApiModelProperty
  private String telephone;
}
