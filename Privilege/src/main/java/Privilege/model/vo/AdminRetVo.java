package Privilege.model.vo;

import Privilege.model.bo.Admin;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AdminRetVo implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "角色")
    private Byte role; //true for female

    @ApiModelProperty(value = "性别")
    private Byte gender; //true for female

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "电话号码")
    private String mobile;

    public AdminRetVo(Admin admin){
        this.id = admin.getId();
        this.role = admin.getRole();
        this.gender = admin.getGender();
        this.userName = admin.getUserName();
        this.email = admin.getDecryptEmail();
        this.mobile = admin.getDecryptMobile();
        this.realName = admin.getRealName();
    }
}
