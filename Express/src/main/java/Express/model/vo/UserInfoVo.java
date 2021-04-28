package Express.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author snow create 2021/04/27 21:25
 *            modified 2021/04/28 08:29
 */
@Data
public class UserInfoVo {

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "学号")
    private String student_number;

    @ApiModelProperty(value = "默认电话")
    private String mobile;

    @ApiModelProperty(value = "默认地址")
    private String address;
}
