package Express.model.bo;

import Express.model.po.DemandPo;
import Express.model.vo.DemandVo;
import Express.util.DemandStatus;
import Core.model.VoObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author snow create 2021/04/15 14:39
 */
@Data
public class Demand implements VoObject, Serializable {

    private Long id;
    private Byte type;
    private Byte status;
    private Byte deleted;
    private Integer price;
    private Long sponsorId;
    private String code;
    private String mobile;
    private String address;
    private String destination;
    private String comment;

    public Demand(DemandPo demandPo){
        this.id = demandPo.getId();
        this.type = demandPo.getType();
        this.status = demandPo.getStatus();
        this.price = demandPo.getPrice();
        this.deleted = demandPo.getDeleted();
        this.sponsorId = demandPo.getSponsorId();
        this.code = demandPo.getCode();
        this.mobile = demandPo.getMobile();
        this.address = demandPo.getAddress();
        this.destination = demandPo.getDestination();
        this.comment = demandPo.getComment();
    }

    public Demand(DemandVo demandVo){
        this.type = demandVo.getType();
        this.status = DemandStatus.UNPAID.getCode();
        this.deleted = (byte)0;
        this.price = demandVo.getPrice();
        this.code = demandVo.getCode();
        this.mobile = demandVo.getMobile();
        this.address = demandVo.getAddress();
        this.destination = demandVo.getDestination();
        this.comment = demandVo.getComment();
    }

    public DemandPo createPo(){
        DemandPo demandPo = new DemandPo();
        demandPo.setId(this.id);
        demandPo.setType(this.type);
        demandPo.setStatus(this.status);
        demandPo.setDeleted(this.deleted);
        demandPo.setPrice(this.price);
        demandPo.setSponsorId(this.sponsorId);
        demandPo.setCode(this.code);
        demandPo.setMobile(this.mobile);
        demandPo.setAddress(this.address);
        demandPo.setDestination(this.destination);
        return demandPo;
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

}
