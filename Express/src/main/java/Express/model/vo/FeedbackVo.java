package Express.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author snow create 2021/04/19 01:04
 */
@Data
public class FeedbackVo {

    @ApiModelProperty(value = "反馈类型")
    @NotNull(message = "反馈类型不能为空")
    private Byte type;

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "反馈内容")
    @NotBlank(message = "反馈内容不能为空")
    private String content;
}
