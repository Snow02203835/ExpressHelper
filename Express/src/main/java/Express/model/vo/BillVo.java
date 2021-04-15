package Express.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author snow create 2021/04/15 16:54
 */
@Data
public class BillVo {

    @ApiModelProperty(value = "支付方式")
    @NotNull(message = "支付方式不能为空")
    private Byte type;

    @ApiModelProperty(value = "支付单号")
    @NotNull(message = "支付单号不能为空")
    private String billID;

}
