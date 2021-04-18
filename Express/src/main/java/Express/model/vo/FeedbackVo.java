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

    @ApiModelProperty(value = "订单id")
    @NotNull(message = "订单id不能为空")
    private Long orderId;

    @ApiModelProperty(value = "反馈内容")
    @NotBlank(message = "反馈内容不能为空")
    private String content;
}
