package Express.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author snow create 2021/04/15 19:56
 */
@Data
public class URLVo {

    @ApiModelProperty(value = "url地址")
    @NotNull(message = "url不能为空")
    private String url;
}
