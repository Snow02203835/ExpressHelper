package Express.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author snow create 2021/04/29 00:55
 */
@Data
public class VerificationVo {

    @ApiModelProperty(value = "封面图片")
    @NotBlank(message = "封面图片不能为空")
    private String coverImg;

    @ApiModelProperty(value = "内页图片")
    @NotBlank(message = "内页图片不能为空")
    private String contentImg;

}
