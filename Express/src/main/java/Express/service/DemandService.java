package Express.service;

import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Express.dao.DemandDao;
import Express.dao.OrderDao;
import Express.model.bo.Bill;
import Express.model.bo.Demand;
import Express.model.bo.Order;
import Express.model.vo.BillVo;
import Express.model.vo.DemandVo;
import Express.model.vo.OrderRetVo;
import Express.util.DemandStatus;
import Express.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemandService {

    private final Long userDepartId = -1L;

    @Autowired
    private DemandDao demandDao;

    @Autowired
    private OrderDao orderDao;

    /**
     * 新建需求
     * @author snow create 2021/04/15 15:07
     * @param userId
     * @param demandVo
     * @return
     */
    public ReturnObject createDemand(Long userId, DemandVo demandVo){
        Demand demand = new Demand(demandVo);
        demand.setSponsorId(userId);
        return demandDao.newDemand(demand);
    }

    /**
     * 用户逻辑删除需求记录
     * @author snow create 2021/04/15 15:45
     *            modified 2021/04/16 00:53
     *            modified 2021/04/16 08:45
     * @param userId
     * @param demandId
     * @return
     */
    public ReturnObject deleteDemandLogically(Long userId, Long demandId){
        ReturnObject<Demand> retObj = demandDao.findDemandById(demandId, false);
        if(retObj.getData() == null){
            return retObj;
        }
        Demand demand = retObj.getData();
        if(!userId.equals(demand.getSponsorId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        if (DemandStatus.SATISFY.getCode().equals(demand.getStatus()) ||
                DemandStatus.UNPAID.getCode().equals(demand.getStatus()) ||
                DemandStatus.CANCEL.getCode().equals(demand.getStatus())){
            demand.setDeleted((byte)1);
            return demandDao.alterDemand(demand);
        }
        else{
            return new ReturnObject(ResponseCode.DEMAND_STATUS_FORBID);
        }
    }

    /**
     * 取消需求
     * @author snow create 2021/04/15 19:37
     *            modified 2021/04/16 00:53
     * @param userId
     * @param departId
     * @param demandId
     * @return
     */
    public ReturnObject cancelDemand(Long userId, Long departId, Long demandId){
        ReturnObject<Demand> retObj = demandDao.findDemandById(demandId, false);
        if(retObj.getData() == null){
            return retObj;
        }
        Demand demand = retObj.getData();
        if(userDepartId.equals(departId) && !userId.equals(demand.getSponsorId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        if(DemandStatus.SATISFY.getCode() > demand.getStatus()){
            if(!DemandStatus.UNPAID.getCode().equals(demand.getStatus())){
                //Refund
            }
            demand.setStatus(DemandStatus.CANCEL.getCode());
            return demandDao.alterDemand(demand);
        }
        else{
            return new ReturnObject(ResponseCode.DEMAND_STATUS_FORBID);
        }
    }

    /**
     * 更新需求状态为待接单（已支付）
     * @author snow create 2021/04/15 16:25
     *            modified 2021/04/15 19:20
     *            modified 2021/04/16 00:53
     * @param userId
     * @param departId
     * @param demandId
     * @param billVo
     * @return
     */
    public ReturnObject userPaidForDemand(Long userId, Long departId, Long demandId, BillVo billVo){
        ResponseCode billStatus = Bill.validateBillStatus(billVo, demandId);
        if(billStatus != ResponseCode.BILL_PAID){
            return new ReturnObject(billStatus);
        }
        ReturnObject<Demand> retObj = demandDao.findDemandById(demandId, false);
        if(retObj.getData() == null){
            return retObj;
        }
        Demand demand = retObj.getData();
        if(userDepartId.equals(departId) && !userId.equals(demand.getSponsorId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        if(DemandStatus.UNPAID.getCode() != demand.getStatus()){
            return new ReturnObject(ResponseCode.DEMAND_STATUS_FORBID);
        }
        demand.setStatus(DemandStatus.EXPECTING.getCode());
        return demandDao.alterDemand(demand);
    }

    /**
     * 根据需求id查找需求详细
     * @author snow create 2021/04/16 00:24
     *            modified 2021/04/16 00:54
     * @param userId
     * @param departId
     * @param demandId
     * @return
     */
    public ReturnObject getDemandById(Long userId, Long departId, Long demandId){
        ReturnObject<Demand> retObj = demandDao.findDemandById(demandId, false);
        if(retObj.getData() == null){
            return retObj;
        }
        Demand demand = retObj.getData();
        if(userDepartId.equals(departId) && !userId.equals(demand.getSponsorId())){
            return retObj;
        }
        demand.setOrders(orderDao.findOrderByDemandId(demandId));
        return new ReturnObject(demand);
    }

    /**
     * 用户接单
     * @author snow create 2021/04/15 16:08
     *            modified 2021/04/16 00:54
     * @param userId
     * @param demandId
     * @return
     */
    public ReturnObject pickUpDemand(Long userId, Long demandId){
        ReturnObject<Demand> retObj = demandDao.findDemandById(demandId, false);
        if(retObj.getData() == null){
            return retObj;
        }
        Demand demand = retObj.getData();
        if(DemandStatus.EXPECTING.getCode() != demand.getStatus()){
            return new ReturnObject(ResponseCode.DEMAND_STATUS_FORBID);
        }
        Order order = new Order(userId, demandId);
        System.out.println(order.toString());
        ReturnObject insertOrderResult = orderDao.insertOrder(order);
        if(insertOrderResult.getData() != null){
            demand.setStatus(DemandStatus.PICKED.getCode());
            ReturnObject updateDemandResult = demandDao.alterDemand(demand);
            if(updateDemandResult.getCode() == ResponseCode.OK){
                return insertOrderResult;
            }
            else{
                return updateDemandResult;
            }
        }
        else{
            return insertOrderResult;
        }
    }

    /**
     * 取消订单
     * @author snow create 2021/04/16 08:28
     * @param userId
     * @param departId
     * @param orderId
     * @return
     */
    @Transactional
    public ReturnObject cancelOrder(Long userId, Long departId, Long orderId){
        ReturnObject<Order> orderReturnObject = orderDao.findOrderById(orderId);
        if(orderReturnObject.getData() == null){
            return orderReturnObject;
        }
        Order order = orderReturnObject.getData();
        if(userDepartId.equals(departId) && !userId.equals(order.getReceiverId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        if(OrderStatus.PICKED.getCode() != order.getStatus()){
            return new ReturnObject(ResponseCode.DEMAND_STATUS_FORBID);
        }
        order.setStatus(OrderStatus.CANCEL.getCode());
        /*
        decrease user credit
         */
        ReturnObject<Demand> demandReturnObject = demandDao.findDemandById(order.getDemandId(), false);
        if (demandReturnObject.getData() == null){
            return demandReturnObject;
        }
        Demand demand = demandReturnObject.getData();
        demand.setStatus(DemandStatus.EXPECTING.getCode());
        demandDao.alterDemand(demand);
        return orderDao.alterOrder(order);
    }

    /**
     * 逻辑删除订单
     * @author snow create 2021/04/16 08:51
     * @param userId
     * @param orderId
     * @return
     */
    public ReturnObject deleteOrderLogically(Long userId, Long orderId){
        ReturnObject<Order> orderReturnObject = orderDao.findOrderById(orderId);
        if(orderReturnObject.getData() == null){
            return orderReturnObject;
        }
        Order order = orderReturnObject.getData();
        if(!userId.equals(order.getReceiverId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        if(OrderStatus.CANCEL.getCode().equals(order.getStatus()) ||
                OrderStatus.SATISFY.getCode().equals(order.getStatus())){
            order.setDeleted((byte)1);
            return orderDao.alterOrder(order);
        }
        else{
            return new ReturnObject(ResponseCode.ORDER_STATUS_FORBID);
        }
    }

    /**
     * 更新订单状态：已接单->已取件 | 已取件->已送达
     * @author snow create 2021/04/15 20:13
     * @param userId
     * @param orderId
     * @param urlCheck
     * @return
     */
    public ReturnObject updateOrderStatusWithURL(Long userId, Long orderId, String urlCheck){
        ReturnObject<Order> retObj = orderDao.findOrderById(orderId);
        if(retObj.getData() == null){
            return retObj;
        }
        Order order = retObj.getData();
        if(!order.getReceiverId().equals(userId)){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        if(!OrderStatus.PICKED.getCode().equals(order.getStatus()) && !OrderStatus.COLLECTED.getCode().equals(order.getStatus())){
            return new ReturnObject(ResponseCode.ORDER_STATUS_FORBID);
        }
        order.setStatus(OrderStatus.PICKED.getCode().equals(order.getStatus()) ? OrderStatus.COLLECTED.getCode() : OrderStatus.SENT.getCode());
        order.setUrlCheck(urlCheck);
        return orderDao.alterOrder(order);
    }

    /**
     * 根据订单id查找订单以及对应需求
     * @author snow create 2021/04/16 01:00
     * @param userId
     * @param departId
     * @param orderId
     * @return
     */
    public ReturnObject getOrderById(Long userId, Long departId, Long orderId){
        ReturnObject<Order> orderReturnObject = orderDao.findOrderById(orderId);
        if(orderReturnObject.getData() == null){
            return orderReturnObject;
        }
        Order order = orderReturnObject.getData();
        if(userDepartId.equals(departId) && !userId.equals(order.getReceiverId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        OrderRetVo orderRetVo = new OrderRetVo(order);
        ReturnObject<Demand> demandReturnObject = demandDao.findDemandById(orderRetVo.getDemandId(), true);
        orderRetVo.addDemandDetail(demandReturnObject.getData());
        return new ReturnObject(orderRetVo);
    }

}
