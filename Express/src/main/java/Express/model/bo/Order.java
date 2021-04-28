package Express.model.bo;

import Core.model.VoObject;
import Express.model.po.OrderPo;
import Express.util.OrderStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author snow create 2021/04/15 16:05
 */
@Data
public class Order implements VoObject, Serializable {

    private Long id;
    private Long demandId;
    private Long receiverId;
    private String receiverMobile;
    private Byte status;
    private Byte deleted;
    private String urlCheck;
    private String urlSent;
    private LocalDateTime pickUpTime;
    private LocalDateTime collectTime;
    private LocalDateTime sentTime;
    private LocalDateTime satisfyTime;
    private LocalDateTime cancelTime;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Order(OrderPo orderPo){
        this.id = orderPo.getId();
        this.demandId = orderPo.getDemandId();
        this.receiverId = orderPo.getReceiverId();
        this.receiverMobile = orderPo.getReceiverMobile();
        this.status = orderPo.getStatus();
        this.deleted = orderPo.getDeleted();
        this.urlCheck = orderPo.getUrlCheck();
        this.urlSent = orderPo.getUrlSent();
        this.pickUpTime = orderPo.getPickUpTime();
        this.collectTime = orderPo.getCollectTime();
        this.sentTime = orderPo.getSentTime();
        this.satisfyTime = orderPo.getSatisfyTime();
        this.cancelTime = orderPo.getCancelTime();
        this.gmtCreate = orderPo.getGmtCreate();
        this.gmtModified = orderPo.getGmtModified();
    }

    public Order(Long receiverId, String mobile, Long demandId){
        this.demandId = demandId;
        this.receiverMobile = mobile;
        this.receiverId = receiverId;
        this.status = OrderStatus.PICKED.getCode();
        this.deleted = (byte)0;
    }

    public OrderPo createPo(){
        OrderPo orderPo = new OrderPo();
        orderPo.setId(this.id);
        orderPo.setDemandId(this.demandId);
        orderPo.setReceiverId(this.receiverId);
        orderPo.setReceiverMobile(this.receiverMobile);
        orderPo.setStatus(this.status);
        orderPo.setDeleted(this.deleted);
        orderPo.setUrlCheck(this.urlCheck);
        orderPo.setUrlSent(this.urlSent);
        orderPo.setPickUpTime(this.pickUpTime);
        orderPo.setCollectTime(this.collectTime);
        orderPo.setSentTime(this.sentTime);
        orderPo.setSatisfyTime(this.satisfyTime);
        orderPo.setCancelTime(this.cancelTime);
        orderPo.setGmtCreate(this.gmtCreate);
        orderPo.setGmtModified(this.gmtModified);
        return orderPo;
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
