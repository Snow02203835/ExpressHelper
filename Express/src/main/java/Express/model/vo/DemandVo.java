package Express.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

/**
 * @author snow create 2021/04/15 14:33
 */
@Data
public class DemandVo {

    @ApiModelProperty(value = "需求类型")
    @NotNull(message = "需求类型不能为空")
    private Byte type;

    @ApiModelProperty(value = "酬金")
    @NotNull(message = "酬金不能为空")
    private Integer price;

    @ApiModelProperty(value = "取件码")
    @NotBlank(message = "取件码不能为空")
    private String code;

    @ApiModelProperty(value = "联系电话")
    @NotBlank(message = "联系电话不能为空")
    private String mobile;

    @ApiModelProperty(value = "取件地址")
    @NotBlank(message = "取件地址不能为空")
    private String address;

    @ApiModelProperty(value = "发布者姓名")
    @NotBlank(message = "发布者姓名不能为空")
    private String sponsorName;

    @ApiModelProperty(value = "送达地址")
    @NotBlank(message = "送达地址不能为空")
    private String destination;

    @ApiModelProperty(value = "预期送达时间")
    private String expectTime;

    @ApiModelProperty(value = "备注")
    private String comment;
}
