package Express.model.vo;

import Core.model.VoObject;
import Express.model.bo.Demand;
import Express.model.bo.Order;
import Express.model.po.DemandPo;
import Express.model.po.OrderPo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author snow create 2021/04/16 00:42
 *            modified 2021/04/26 23:57
 */
@Data
public class OrderRetVo implements VoObject, Serializable {

    private Long orderId;
    private Byte type;
    private Byte status;
    private Integer price;
    private Integer campusId;
    private Long demandId;
    private Long sponsorId;
    private Long receiverId;
    private String receiverMobile;
    private String urlCheck;
    private String urlSent;
    private String code;
    private String mobile;
    private String company;
    private String address;
    private String sponsorName;
    private String destination;
    private String expectTime;
    private String comment;
    private LocalDateTime pickUpTime;
    private LocalDateTime collectTime;
    private LocalDateTime sentTime;
    private LocalDateTime satisfyTime;
    private LocalDateTime cancelTime;

    public OrderRetVo(Order order){
        this.orderId = order.getId();
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

    public OrderRetVo(OrderPo orderPo){
        this.orderId = orderPo.getId();
        this.demandId = orderPo.getDemandId();
        this.receiverId = orderPo.getReceiverId();
        this.receiverMobile = orderPo.getReceiverMobile();
        this.status = orderPo.getStatus();
        this.urlCheck = orderPo.getUrlCheck();
        this.urlSent = orderPo.getUrlSent();
        this.pickUpTime = orderPo.getPickUpTime();
        this.collectTime = orderPo.getCollectTime();
        this.sentTime = orderPo.getSentTime();
        this.satisfyTime = orderPo.getSatisfyTime();
        this.cancelTime = orderPo.getCancelTime();
    }

    public OrderRetVo(DemandPo demandPo) {
        this.demandId = demandPo.getId();
        this.type = demandPo.getType();
        this.price = demandPo.getPrice();
        this.campusId = demandPo.getCampusId();
        this.sponsorId = demandPo.getSponsorId();
        this.code = demandPo.getCode();
        this.mobile = demandPo.getMobile();
        this.company = demandPo.getCompany();
        this.address = demandPo.getAddress();
        this.sponsorName = demandPo.getSponsorName();
        this.destination = demandPo.getDestination();
        this.expectTime = demandPo.getExpectTime();
        this.comment = demandPo.getComment();
    }

    public void addOrderDetail(Order order){
        if(order == null){
            return;
        }
        this.orderId = order.getId();
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
        this.campusId = demand.getCampusId();
        this.sponsorId = demand.getSponsorId();
        this.code = demand.getCode();
        this.mobile = demand.getMobile();
        this.company = demand.getCompany();
        this.address = demand.getAddress();
        this.sponsorName = demand.getSponsorName();
        this.destination = demand.getDestination();
        this.expectTime = demand.getExpectTime();
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
