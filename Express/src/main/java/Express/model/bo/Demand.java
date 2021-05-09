package Express.model.bo;

import Express.model.po.DemandPo;
import Express.model.vo.DemandVo;
import Express.util.DemandStatus;
import Core.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author snow create 2021/04/15 14:39
 *            modified 2021/04/19 11:44
 *            modified 2021/04/28 08:33
 */
@Data
public class Demand implements VoObject, Serializable {

    private Long id;
    private Byte type;
    private Byte status;
    private Byte deleted;
    private Integer price;
    private Integer campusId;
    private Long sponsorId;
    private String code;
    private String mobile;
    private String company;
    private String address;
    private String sponsorName;
    private String destination;
    private String expectTime;
    private String comment;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private List<Order> orders;

    public Demand(DemandPo demandPo){
        this.id = demandPo.getId();
        this.type = demandPo.getType();
        this.status = demandPo.getStatus();
        this.price = demandPo.getPrice();
        this.campusId = demandPo.getCampusId();
        this.deleted = demandPo.getDeleted();
        this.sponsorId = demandPo.getSponsorId();
        this.code = demandPo.getCode();
        this.mobile = demandPo.getMobile();
        this.company = demandPo.getCompany();
        this.address = demandPo.getAddress();
        this.sponsorName = demandPo.getSponsorName();
        this.destination = demandPo.getDestination();
        this.expectTime = demandPo.getExpectTime();
        this.comment = demandPo.getComment();
        this.gmtCreate = demandPo.getGmtCreate();
        this.gmtModified = demandPo.getGmtModified();
    }

    public Demand(DemandVo demandVo){
        this.type = demandVo.getType();
        this.status = DemandStatus.UNPAID.getCode();
        this.deleted = (byte)0;
        this.price = demandVo.getPrice();
        this.campusId = demandVo.getCampusId();
        this.code = demandVo.getCode();
        this.mobile = demandVo.getMobile();
        this.company = demandVo.getCompany();
        this.address = demandVo.getAddress();
        this.sponsorName = demandVo.getSponsorName();
        this.destination = demandVo.getDestination();
        this.expectTime = demandVo.getExpectTime();
        this.comment = demandVo.getComment();
    }

    public DemandPo createPo(){
        DemandPo demandPo = new DemandPo();
        demandPo.setId(this.id);
        demandPo.setType(this.type);
        demandPo.setStatus(this.status);
        demandPo.setDeleted(this.deleted);
        demandPo.setPrice(this.price);
        demandPo.setCampusId(this.campusId);
        demandPo.setSponsorId(this.sponsorId);
        demandPo.setCode(this.code);
        demandPo.setMobile(this.mobile);
        demandPo.setCompany(this.company);
        demandPo.setAddress(this.address);
        demandPo.setSponsorName(this.sponsorName);
        demandPo.setDestination(this.destination);
        demandPo.setExpectTime(this.expectTime);
        demandPo.setGmtCreate(this.gmtCreate);
        demandPo.setGmtModified(this.gmtModified);
        return demandPo;
    }

    public void updateFieldWithVo(DemandVo demandVo){
        if(demandVo.getType() != null) {
            this.type = demandVo.getType();
        }
        if(demandVo.getPrice() != null) {
            this.price = demandVo.getPrice();
        }
        if(demandVo.getCampusId() != null){
            this.campusId = demandVo.getCampusId();
        }
        if(demandVo.getCode() != null && !demandVo.getCode().isBlank()) {
            this.code = demandVo.getCode();
        }
        if(demandVo.getMobile() != null && !demandVo.getMobile().isBlank()) {
            this.mobile = demandVo.getMobile();
        }
        if(demandVo.getCompany() != null && !demandVo.getCompany().isBlank()){
            this.company = demandVo.getCompany();
        }
        if(demandVo.getAddress() != null && !demandVo.getAddress().isBlank()) {
            this.address = demandVo.getAddress();
        }
        if(demandVo.getSponsorName() != null && !demandVo.getSponsorName().isBlank()){
            this.sponsorName = demandVo.getSponsorName();
        }
        if(demandVo.getDestination() != null && !demandVo.getDestination().isBlank()) {
            this.destination = demandVo.getDestination();
        }
        if(demandVo.getExpectTime() != null){
            this.expectTime = demandVo.getExpectTime();
        }
        if(demandVo.getComment() != null) {
            this.comment = demandVo.getComment();
        }
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
