package Express.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author snow create 2021/04/19 15:04
 */
@Data
public class FeedbackResponseVo {
    @ApiModelProperty(value = "反馈答复")
    @NotBlank(message = "反馈答复不能为空")
    private String response;
}
