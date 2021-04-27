package Express.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author snow create 2021/04/27 21:25
 */
@Data
public class UserInfoVo {

    @ApiModelProperty(value = "默认电话")
    private String mobile;

    @ApiModelProperty(value = "默认地址")
    private String address;
}
