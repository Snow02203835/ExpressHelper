package Express.model.vo;

import Core.model.VoObject;
import Express.model.bo.Demand;
import Express.model.bo.Order;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author snow create 2021/04/16 00:42
 *            modified 2021/04/26 23:57
 */
@Data
public class OrderRetVo implements VoObject, Serializable {

    private Long id;
    private Byte type;
    private Byte status;
    private Integer price;
    private Long demandId;
    private Long sponsorId;
    private Long receiverId;
    private String receiverMobile;
    private String urlCheck;
    private String urlSent;
    private String code;
    private String mobile;
    private String address;
    private String sponsorName;
    private String destination;
    private String comment;
    private LocalDateTime pickUpTime;
    private LocalDateTime collectTime;
    private LocalDateTime sentTime;
    private LocalDateTime satisfyTime;
    private LocalDateTime cancelTime;

    public OrderRetVo(Order order){
        this.id = order.getId();
        this.demandId = order.getDemandId();
        this.receiverId = order.getReceiverId();
        this.receiverMobile = order.getReceiverMobile();
        this.status = order.getStatus();
        this.urlCheck = order.getUrlCheck();
        this.urlSent = order.getUrlSent();
        this.pickUpTime = order.getPickUpTime();
        this.collectTime = order.getCollectTime();
        this.sentTime = order.getSentTime();
        this.satisfyTime = order.getSatisfyTime();
        this.cancelTime = order.getCancelTime();
    }

    public void addDemandDetail(Demand demand){
        if(demand == null){
            return;
        }
        this.type = demand.getType();
        this.price = demand.getPrice();
        this.sponsorId = demand.getSponsorId();
        this.code = demand.getCode();
        this.mobile = demand.getMobile();
        this.address = demand.getAddress();
        this.sponsorName = demand.getSponsorName();
        this.destination = demand.getDestination();
        this.comment = demand.getComment();
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
