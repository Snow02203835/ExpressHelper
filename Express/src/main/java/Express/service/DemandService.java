package Express.service;

import Core.model.VoObject;
import Core.util.JwtHelper;
import Core.util.MD5;
import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Express.dao.*;
import Express.model.bo.*;
import Express.model.po.DemandPo;
import Express.model.po.FeedBackPo;
import Express.model.po.OrderPo;
import Express.model.vo.*;
import Express.util.DemandStatus;
import Express.util.FeedbackStatus;
import Express.util.OrderStatus;
import Express.util.VerificationStatus;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DemandService {

    private final Long userDepartId = -1L;
    private final Long userSelfCode = -3835L;

    @Autowired
    private UserDao userDao;
    @Autowired
    private DemandDao demandDao;
    @Autowired
    private FeedbackDao feedbackDao;
    @Autowired
    private ImageDao imageDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private StaticDao staticDao;
    @Autowired
    private VerificationDao verificationDao;

    @Value("${Express.img.path}")
    private String imgPath;
    @Value("${Express.user.appId}")
    private String appID;
    @Value("${Express.user.appSecret}")
    private String appSecret;
    @Value("${Express.user.login.jwtExpire}")
    private Integer jwtExpireTime;
    @Value("${Express.user.illegalCancelDemand}")
    private Integer illegalCancelDemand;
    @Value("${Express.user.illegalCancelOrders}")
    private Integer illegalCancelOrders;

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
     * 更新需求
     * @author snow create 2021/04/16 09:06
     * @param userId
     * @param departId
     * @param demandId
     * @param demandVo
     * @return
     */
    public ReturnObject updateDemand(Long userId, Long departId, Long demandId, DemandVo demandVo){
        ReturnObject<Demand> demandReturnObject = demandDao.findDemandById(demandId, false);
        if(demandReturnObject.getData() == null){
            return demandReturnObject;
        }
        Demand demand = demandReturnObject.getData();
        if(userDepartId.equals(departId) && !userId.equals(demand.getSponsorId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        if(DemandStatus.UNPAID.getCode().equals(demand.getStatus()) || DemandStatus.EXPECTING.getCode().equals(demand.getStatus())) {
            demand.updateFieldWithVo(demandVo);
            return demandDao.alterDemand(demand);
        }
        else {
            return new ReturnObject(ResponseCode.DEMAND_STATUS_FORBID);
        }
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
     *            modified 2021/04/26 15:29
     *            modified 2021/05/08 16:54
     *            modified 2021/05/20 00:52
     * @param userId 用户id
     * @param departId 角色id
     * @param demandId 需求id
     * @return 操作结果
     */
    @Transactional
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
                if(DemandStatus.PICKED.getCode().equals(demand.getStatus())){
                    List<Order> orderList = orderDao.findOrderByDemandId(demandId);
                    if(orderList == null){
                        return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
                    }
                    Byte orderStatus = orderList.get(orderList.size()-1).getStatus();
                    if(OrderStatus.PICKED.getCode().equals(orderStatus)){
                        decreaseUserCredit(userId, illegalCancelDemand);
                        Order order = orderList.get(orderList.size()-1);
                        order.setStatus(OrderStatus.BEEN_CANCEL.getCode());
                        orderDao.alterOrder(order);
                    }
                    else if(!OrderStatus.CANCEL.getCode().equals(orderStatus)){
                        return new ReturnObject(ResponseCode.DEMAND_STATUS_FORBID);
                    }
                }
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
     *            modified 2021/05/25 14:06
     *            modified 2021/05/25 15:42
     * @param userId 用户id
     * @param departId 角色id
     * @param demandId 需求id
     * @return 需求详细
     */
    public ReturnObject getDemandById(Long userId, Long departId, Long demandId){
        ReturnObject<Demand> retObj = demandDao.findDemandById(demandId, false);
        if(retObj.getData() == null){
            return retObj;
        }
        Demand demand = retObj.getData();
        if(userDepartId.equals(departId) && !userId.equals(demand.getSponsorId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        if(!DemandStatus.CANCEL.getCode().equals(demand.getStatus())) {
            if (userDepartId.equals(departId)){
                Order order = orderDao.findLastOrderByDemandId(demandId);
                if(order != null && !OrderStatus.CANCEL.getCode().equals(order.getStatus())){
                    List<Order> orders = new ArrayList<>(1);
                    orders.add(order);
                    demand.setOrders(orders);
                }
            }
            else {
                demand.setOrders(orderDao.findOrderByDemandId(demandId));
            }
        }
        return new ReturnObject(demand);
    }

    /**
     * 根据条件查找需求
     * @author snow create 2021/04/16 11:08
     *            modified 2021/04/17 00:49
     *            modified 2021/04/30 15:58
     *            modified 2021/05/09 19:41
     *            modified 2021/05/25 14:02
     * @param userId 用户id
     * @param departId 角色id
     * @param sponsorId 发布者id
     * @param type 类型
     * @param status 状态
     * @param deleted 逻辑删除是否可见
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param campusId 校区id
     * @param address 取件地址
     * @param destination 送达地址
     * @param startTime 开始时间
     * @param endTime 结果时间
     * @param page 页号
     * @param pageSize 页大小
     * @return 查询分页结果
     */
    public ReturnObject<PageInfo<VoObject>> getDemandsWithCondition(Long userId, Long departId, Long sponsorId, Byte type, Byte status,
                                                                    Byte deleted, Integer minPrice, Integer maxPrice, Integer campusId,
                                                                    String address, String destination,
                                                                    LocalDateTime startTime, LocalDateTime endTime,
                                                                    Integer page, Integer pageSize){
        if(userDepartId.equals(departId)){
            if(userSelfCode.equals(sponsorId)){
                sponsorId = userId;
            }
            else{
                status = DemandStatus.EXPECTING.getCode();
            }
            deleted = (byte)0;
        }
        PageHelper.startPage(page, pageSize);
        PageInfo<DemandPo> demandPoPageInfo = demandDao.findDemandsWithCondition(sponsorId, type, status, deleted,
                minPrice, maxPrice, campusId, address, destination, startTime, endTime);
        if(demandPoPageInfo == null){
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        List<OrderRetVo> demandList = demandPoPageInfo.getList().stream().map(OrderRetVo::new).collect(Collectors.toList());
        List<VoObject> demandInfo = new ArrayList<>(demandList.size());
        for(OrderRetVo demandVo : demandList){
            if(!DemandStatus.CANCEL.getCode().equals(demandVo.getDemandStatus())){
                demandVo.addOrderDetail(orderDao.findLastOrderByDemandId(demandVo.getDemandId()));
            }
            demandInfo.add(demandVo);
        }
        PageInfo<VoObject> retObj = new PageInfo<>(demandInfo);
        retObj.setPages(demandPoPageInfo.getPages());
        retObj.setPageNum(demandPoPageInfo.getPageNum());
        retObj.setPageSize(demandPoPageInfo.getPageSize());
        retObj.setTotal(demandPoPageInfo.getTotal());

        return new ReturnObject<>(retObj);
    }

    /**
     * 用户接单
     * @author snow create 2021/04/15 16:08
     *            modified 2021/04/16 00:54
     *            modified 2021/04/28 09:24
     *            modified 2021/04/30 17:04
     *            modified 2021/05/23 20:08
     *            modified 2021/05/25 13:52
     * @param userId 用户id
     * @param demandId 需求id
     * @return 订单视图
     */
    public ReturnObject pickUpDemand(Long userId, Long demandId){
        ReturnObject<Demand> retObj = demandDao.findDemandById(demandId, false);
        if(retObj.getData() == null){
            return retObj;
        }
        Demand demand = retObj.getData();
        if(!DemandStatus.EXPECTING.getCode().equals(demand.getStatus())){
            return new ReturnObject(ResponseCode.DEMAND_STATUS_FORBID);
        }
        if(demand.getSponsorId().equals(userId)){
            return new ReturnObject(ResponseCode.DEMAND_BELONG_SELF);
        }
        ReturnObject<User> userRetObj = userDao.findUserById(userId);
        if(userRetObj.getData() == null){
            return userRetObj;
        }
        User user = userRetObj.getData();
        if(user.getStudentVerify().equals((byte)0)){
            return new ReturnObject(ResponseCode.USER_STUDENT_NOT_VERIFY);
        }
        if(user.getMobile() == null || user.getDecryptMobile() == null){
            return new ReturnObject(ResponseCode.MOBILE_EMPTY);
        }
        Order order = new Order(userId, user.getDecryptMobile(), demandId);
        order.setPickUpTime(LocalDateTime.now());
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
     *            modified 2021/04/28 09:23
     *            modified 2021/04/30 17:21
     *            modified 2021/05/08 16:57
     * @param userId 用户id
     * @param departId 角色id
     * @param orderId 订单id
     * @return 操作结果
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
        if(!OrderStatus.PICKED.getCode().equals(order.getStatus())){
            return new ReturnObject(ResponseCode.ORDER_STATUS_FORBID);
        }
        order.setStatus(OrderStatus.CANCEL.getCode());
        order.setCancelTime(LocalDateTime.now());
        decreaseUserCredit(userId, illegalCancelOrders);
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
     *            modified 2021/04/28 09:2
     * @param userId 用户id
     * @param orderId 订单id
     * @param url 图片地址
     * @return 操作结果
     */
    public ReturnObject updateOrderStatusWithURL(Long userId, Long orderId, String url){
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
        if(OrderStatus.PICKED.getCode().equals(order.getStatus())){
            order.setStatus(OrderStatus.COLLECTED.getCode());
            order.setUrlCheck(url);
            order.setCollectTime(LocalDateTime.now());
        }
        else{
            order.setStatus(OrderStatus.SENT.getCode());
            order.setUrlSent(url);
            order.setSentTime(LocalDateTime.now());
        }
        return orderDao.alterOrder(order);
    }

    /**
     * 需求发布者确认订单已完成
     * @author snow create 2021/05/21 16:04
     *            modified 2021/05/23 20:03
     * @param userId 用户id
     * @param orderId 订单id
     * @return 操作结果
     */
    public ReturnObject sponsorConfirmOrder(Long userId, Long orderId){
        ReturnObject<Order> orderReturnObject = orderDao.findOrderById(orderId);
        if(orderReturnObject.getData() == null){
            return orderReturnObject;
        }
        Order order = orderReturnObject.getData();
        ReturnObject<Demand> demandReturnObject = demandDao.findDemandById(order.getDemandId(), false);
        if (demandReturnObject.getData() == null){
            return demandReturnObject;
        }
        ReturnObject<User> userReturnObject = userDao.findUserById(order.getReceiverId());
        if(userReturnObject.getData() == null){
            return userReturnObject;
        }
        User user = userReturnObject.getData();
        Demand demand = demandReturnObject.getData();
        if(!userId.equals(demand.getSponsorId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        user.successfullyDeliverPackage();
        userDao.updateUserInfo(user);
        order.setStatus(OrderStatus.SATISFY.getCode());
        demand.setStatus(DemandStatus.SATISFY.getCode());
        orderDao.alterOrder(order);
        return demandDao.alterDemand(demand);
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

    /**
     * 根据条件查找订单
     * @author snow create 2021/04/17 00:39
     *            modified 2021/05/06 16:47
     *            modified 2021/05/20 00:59
     *            modified 2021/05/26 15:18
     * @param userId 用户id
     * @param departId 角色id
     * @param receiverId 接单者id
     * @param status 状态
     * @param deleted 逻辑删除是否可见
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param pageSize 页大小
     * @return 查找结果
     */
    public ReturnObject<PageInfo<VoObject>> getOrdersWithCondition(Long userId, Long departId, Long receiverId, Byte status,
                                                      Byte deleted, LocalDateTime startTime, LocalDateTime endTime,
                                                      Integer page, Integer pageSize){
        if(userDepartId.equals(departId)){
            deleted = (byte)0;
            receiverId = userId;
        }
        PageHelper.startPage(page, pageSize);
        PageInfo<OrderPo> orderPoPageInfo = orderDao.findOrdersWithCondition(receiverId, status, deleted, startTime, endTime);
        if(orderPoPageInfo == null){
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        List<OrderRetVo> orderList = orderPoPageInfo.getList().stream().map(OrderRetVo::new).collect(Collectors.toList());
        List<VoObject> orderInfos = new ArrayList<>(orderList.size());
        for(OrderRetVo orderVo : orderList){
            if (!userDepartId.equals(departId) || (
                    !OrderStatus.CANCEL.getCode().equals(orderVo.getOrderStatus()) &&
                    !OrderStatus.BEEN_CANCEL.getCode().equals(orderVo.getOrderStatus()))) {
                orderVo.addDemandDetail(demandDao.findDemandById(orderVo.getDemandId(), true).getData());
            }
            orderInfos.add(orderVo);
        }
        System.out.println(orderInfos.toString());

        PageInfo<VoObject> retObj = new PageInfo<>(orderInfos);
        retObj.setPages(orderPoPageInfo.getPages());
        retObj.setPageNum(orderPoPageInfo.getPageNum());
        retObj.setPageSize(orderPoPageInfo.getPageSize());
        retObj.setTotal(orderPoPageInfo.getTotal());

        return new ReturnObject<>(retObj);

    }

    /**
     * 用户反馈
     * @author snow create 2021/04/19 01:32
     *            modified 2021/04/19 11:20
     *            modified 2021/04/20 19:31
     *            modified 2021/05/06 13:44
     * @param userId 用户id
     * @param feedbackVo 反馈信息
     * @return 插入结果
     */
    public ReturnObject userFeedback(Long userId, FeedbackVo feedbackVo){
        if(feedbackVo.getOrderId() != null){
            ReturnObject<Order> orderReturnObject = orderDao.findOrderById(feedbackVo.getOrderId());
            if(orderReturnObject.getData() == null){
                return orderReturnObject;
            }
            Order order = orderReturnObject.getData();
            if(!order.getReceiverId().equals(userId)){
                ReturnObject<Demand> demandReturnObject = demandDao.findDemandById(order.getDemandId(), false);
                if (demandReturnObject.getData() == null){
                    return demandReturnObject;
                }
                Demand demand = demandReturnObject.getData();
                if(!demand.getSponsorId().equals(userId)){
                    return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
                }
            }
        }
        Feedback feedback = new Feedback(userId, feedbackVo);
        return feedbackDao.insertFeedback(feedback);
    }

    /**
     * 用户更新反馈内容
     * @author snow create 2021/04/19 13:11
     *            modified 2021/04/20 19:58
     *            modified 2021/05/14 11:11
     * @param userId 用户id
     * @param feedbackId 反馈信息id
     * @param img 上传图片
     * @param content 新反馈内容
     * @return  操作结果
     */
    public ReturnObject userUpdateFeedbackContent(Long userId, Long feedbackId, String img, String content){
        ReturnObject<Feedback> retObj = feedbackDao.findFeedbackById(feedbackId);
        if(retObj.getData() == null){
            return retObj;
        }
        Feedback feedback = retObj.getData();
        if(!userId.equals(feedback.getUserId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        if(FeedbackStatus.HANDLING.getCode() < feedback.getStatus()){
            return new ReturnObject(ResponseCode.FEEDBACK_STATUS_FORBID);
        }
        if(img != null){
            feedback.setImg(img);
        }
        if(content != null) {
            feedback.setContent(content);
        }
        return feedbackDao.updateFeedback(feedback);
    }

    /**
     * 用户取消反馈
     * @author snow create 2021/04/19 14:53
     * @param userId 用户id
     * @param feedbackId 反馈id
     * @return 操作结果
     */
    public ReturnObject userCancelFeedback(Long userId, Long feedbackId){
        ReturnObject<Feedback> retObj = feedbackDao.findFeedbackById(feedbackId);
        if(retObj.getData() == null){
            return retObj;
        }
        Feedback feedback = retObj.getData();
        if(!userId.equals(feedback.getUserId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        if(FeedbackStatus.EXPECTING.getCode().equals(feedback.getStatus()) ||
                FeedbackStatus.HANDLING.getCode().equals(feedback.getStatus())){
            feedback.setStatus(FeedbackStatus.CANCEL.getCode());
            return feedbackDao.updateFeedback(feedback);
        }
        else{
            return new ReturnObject(ResponseCode.FEEDBACK_STATUS_FORBID);
        }
    }

    /**
     * 用户删除反馈
     * @author snow create 2021/04/19 14:55
     * @param userId 用户id
     * @param feedbackId 反馈id
     * @return 操作结果
     */
    public ReturnObject userDeleteFeedback(Long userId, Long feedbackId){
        ReturnObject<Feedback> retObj = feedbackDao.findFeedbackById(feedbackId);
        if(retObj.getData() == null){
            return retObj;
        }
        Feedback feedback = retObj.getData();
        if(!userId.equals(feedback.getUserId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
        }
        if(FeedbackStatus.RESPONDED.getCode().equals(feedback.getStatus()) ||
                FeedbackStatus.CANCEL.getCode().equals(feedback.getStatus())){
            feedback.setDeleted((byte)1);
            return feedbackDao.updateFeedback(feedback);
        }
        else{
            return new ReturnObject(ResponseCode.FEEDBACK_STATUS_FORBID);
        }
    }

    /**
     * 管理员受理用户反馈
     * @author snow create 2021/04/19 14:59
     * @param feedbackId 反馈id
     * @return 操作结果
     */
    public ReturnObject adminHandlingFeedback(Long feedbackId){
        ReturnObject<Feedback> retObj = feedbackDao.findFeedbackById(feedbackId);
        if(retObj.getData() == null){
            return retObj;
        }
        Feedback feedback = retObj.getData();
        if(FeedbackStatus.EXPECTING.getCode().equals(feedback.getStatus())){
            feedback.setStatus(FeedbackStatus.HANDLING.getCode());
            return feedbackDao.updateFeedback(feedback);
        }
        else{
            return new ReturnObject(ResponseCode.FEEDBACK_STATUS_FORBID);
        }
    }

    /**
     * 管理员答复用户反馈
     * @author snow create 2021/04/19 15:00
     * @param feedbackId 反馈id
     * @param response 答复内容
     * @return 操作结果
     */
    public ReturnObject adminRespondFeedback(Long feedbackId, String response){
        ReturnObject<Feedback> retObj = feedbackDao.findFeedbackById(feedbackId);
        if(retObj.getData() == null){
            return retObj;
        }
        Feedback feedback = retObj.getData();
        if(FeedbackStatus.EXPECTING.getCode().equals(feedback.getStatus()) ||
                FeedbackStatus.HANDLING.getCode().equals(feedback.getStatus())){
            feedback.setResponse(response);
            feedback.setStatus(FeedbackStatus.RESPONDED.getCode());
            return feedbackDao.updateFeedback(feedback);
        }
        else{
            return new ReturnObject(ResponseCode.FEEDBACK_STATUS_FORBID);
        }
    }

    /**
     * 根据反馈id返回反馈详情
     * @author snow create 2021/04/19 01:37
     *            modified 2021/04/19 10:30
     * @param userId 用户id
     * @param departId 角色id
     * @param feedbackId 反馈id
     * @return 反馈详情
     */
    public ReturnObject getUserFeedbackById(Long userId, Long departId, Long feedbackId){
        ReturnObject<Feedback> retObj = feedbackDao.findFeedbackById(feedbackId);
        if(retObj.getData() != null){
            Feedback feedback = retObj.getData();
            if(userDepartId.equals(departId) && !userId.equals(feedback.getUserId())){
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
            }
        }
        return retObj;
    }

    /**
     * 根据订单id返回反馈详情
     * @author snow create 2021/04/19 10:42
     * @param userId 用户id
     * @param departId 角色id
     * @param orderId 订单id
     * @return 反馈详情
     */
    public ReturnObject getUserFeedbackByOrderId(Long userId, Long departId, Long orderId){
        ReturnObject<Feedback> retObj = feedbackDao.findFeedbackByOrderId(orderId);
        if(retObj.getData() != null){
            Feedback feedback = retObj.getData();
            if(userDepartId.equals(departId) && !userId.equals(feedback.getUserId())){
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUT_SCOPE);
            }
        }
        return retObj;
    }

    /**
     * 根据条件查找用户反馈
     * @author snow create 2021/04/19 15:27
     * @param userId 用户id
     * @param departId 角色id
     * @param ownerId 反馈所有者id
     * @param type 反馈类型
     * @param status 反馈状态
     * @param deleted 逻辑删除反馈是否可见
     * @param content 反馈内容
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param pageSize 页大小
     * @return 查找结果
     */
    public ReturnObject<PageInfo<VoObject>> getUserFeedbackWithCondition(Long userId, Long departId, Long ownerId,
                                                                         Byte type, Byte status, Byte deleted, String content,
                                                                         LocalDateTime startTime, LocalDateTime endTime,
                                                                         Integer page, Integer pageSize){
        if(userDepartId.equals(departId)){
            deleted = (byte)0;
            ownerId = userId;
        }
        PageHelper.startPage(page, pageSize);
        PageInfo<FeedBackPo> feedBackPoPageInfo = feedbackDao.findFeedbackWithCondition(ownerId, type,
                status, deleted, content, startTime, endTime);
        if(feedBackPoPageInfo == null){
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        List<VoObject> feedbackInfos = feedBackPoPageInfo.getList().stream().map(Feedback::new).collect(Collectors.toList());

        PageInfo<VoObject> retObj = new PageInfo<>(feedbackInfos);
        retObj.setPages(feedBackPoPageInfo.getPages());
        retObj.setPageNum(feedBackPoPageInfo.getPageNum());
        retObj.setPageSize(feedBackPoPageInfo.getPageSize());
        retObj.setTotal(feedBackPoPageInfo.getTotal());

        return new ReturnObject<>(retObj);
    }

    /**
     * 根据code从微信获取openId
     * @author snow create 2021/04/27 20:37
     * @param code 前端获取的code
     * @return openId
     */
    public String getOpenIdFromWeChat(String code){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String resultString = "";
        CloseableHttpResponse response = null;
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appID + "&secret=" +
                appSecret + "&js_code=" + code + "&grant_type=authorization_code";
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            URI uri = builder.build();
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 解析json
        JSONObject jsonObject = (JSONObject) JSONObject.parse(resultString);
        return jsonObject.get("openid").toString();
    }

    /**
     * 用户登录
     * @author snow create 2021/04/27 21:05
     * @param code 前端获取的一次性code
     * @return 登录结果
     */
    @Transactional
    public ReturnObject userLogin(String code){
        String openId = getOpenIdFromWeChat(code);
        ReturnObject<User> userReturnObject = userDao.findUserByOpenId(openId);
        User user;
        if (userReturnObject.getCode() == ResponseCode.INTERNAL_SERVER_ERR){
            return userReturnObject;
        }
        else{
            if(userReturnObject.getData() != null){
                user = userReturnObject.getData();
                if (user.isSignatureBeenModify()){
                    return new ReturnObject(ResponseCode.RESOURCE_FALSIFY);
                }
            }
            else{
                user = new User(openId);
                ReturnObject retObj = userDao.insertNewUser(user);
                if(retObj.getCode() != ResponseCode.OK){
                    return retObj;
                }
            }
            String token = new JwtHelper().createToken(user.getId(), userDepartId, jwtExpireTime);
            return new ReturnObject(new UserLoginRetVo(token, user));
        }
    }

    /**
     * 扣除/增加用户信誉分
     * @author snow create 2021/05/08 16:46
     * @param userId 用户id
     * @param credit 扣除量
     * @return 操作结果错误码
     */
    private ResponseCode decreaseUserCredit(Long userId, Integer credit){
        ReturnObject<User> retObj = userDao.findUserById(userId);
        if(retObj.getData() == null){
            return retObj.getCode();
        }
        User user = retObj.getData();
        user.decreaseCredit(credit);
        return userDao.updateUserInfo(user);
    }

    /**
     * 用户更新自身信息
     * @author snow create 2021/04/27 21:42
     *            modified 2021/04/29 14:34
     *            modified 2021/04/29 15:10
     * @param userId 用户id
     * @param userInfo 用户信息
     * @return 操作结果
     */
    @Transactional
    public ResponseCode userUpdateSelfInfo(Long userId, UserInfoVo userInfo){
        ReturnObject<User> userReturnObject = userDao.findUserById(userId);
        if(userReturnObject.getData() == null){
            return userReturnObject.getCode();
        }
        User user = userReturnObject.getData();
        System.out.println(user.toString());
        if((userInfo.getName() == null || userInfo.getName().isBlank()) &&
                (userInfo.getMobile() == null || userInfo.getMobile().isBlank()) &&
                (userInfo.getAddress() == null || userInfo.getAddress().isBlank()) &&
                (userInfo.getStudentNumber() == null || userInfo.getStudentNumber().isBlank())){
            return ResponseCode.FIELD_NOT_VALID;
        }
        user.updateInfoSelective(userInfo);
        return userDao.updateUserInfo(user);
    }

    /**
     * 用户提交学生认证信息
     * @author snow create 2021/04/29 10:17
     * @param userId 用户id
     * @param verificationVo 认证信息
     * @return 提交结果
     */
    @Transactional
    public ReturnObject userCommitVerification(Long userId, VerificationVo verificationVo){
        ReturnObject<List<Verification>> retObjs = verificationDao.findVerificationByUserId(userId);
        if(retObjs.getCode() == ResponseCode.INTERNAL_SERVER_ERR){
            return retObjs;
        }
        if(retObjs.getData() != null){
            List<Verification> verifications = retObjs.getData();
            if(!VerificationStatus.FAILED.getCode().equals(verifications.get(verifications.size()-1).getStatus())){
                return new ReturnObject(ResponseCode.REPEAT_COMMIT_VERIFICATION);
            }
        }
        Verification verification = new Verification(userId, verificationVo);
        ResponseCode code = verificationDao.insertNewVerification(verification);
        if(code == ResponseCode.OK){
            return new ReturnObject(verification);
        }
        else{
            return new ReturnObject(code);
        }
    }

    /**
     * 用户修改自己的未处理/未通过认证信息
     * @author snow create 2021/04/29 14:11
     * @param userId 用户id
     * @param verificationId 认证id
     * @param verificationVo 认证信息
     * @return 操作结果错误码
     */
    @Transactional
    public ResponseCode userAlterVerificationInfo(Long userId, Long verificationId, VerificationVo verificationVo){
        if(verificationVo.getCoverImg().isBlank() && verificationVo.getContentImg().isBlank()){
            return ResponseCode.FIELD_NOT_VALID;
        }
        ReturnObject<Verification> retObj = verificationDao.findVerificationById(verificationId);
        if(retObj.getData() == null){
            return retObj.getCode();
        }
        Verification verification = retObj.getData();
        if(!userId.equals(verification.getUserId())){
            return ResponseCode.RESOURCE_ID_OUT_SCOPE;
        }
        if(VerificationStatus.UNHANDLED.getCode().equals(verification.getStatus()) ||
                VerificationStatus.FAILED.getCode().equals(verification.getStatus())){
            verification.updateInfoWithVo(verificationVo);
            return verificationDao.updateVerificationInfo(verification);
        }
        else{
            return ResponseCode.VERIFICATION_STATUS_FORBID;
        }
    }

    /**
     * 管理员审核用户学生认证
     * @author snow create 2021/04/29 10:44
     * @param verificationId 认证id
     * @param pass 是否通过
     * @return 操作结果错误码
     */
    @Transactional
    public ResponseCode adminAuditUserVerification(Long verificationId, Boolean pass){
        ReturnObject<Verification> verificationRetObj = verificationDao.findVerificationById(verificationId);
        if (verificationRetObj.getData() == null){
            return verificationRetObj.getCode();
        }
        Verification verification = verificationRetObj.getData();
        if(!VerificationStatus.UNHANDLED.getCode().equals(verification.getStatus())){
            return ResponseCode.VERIFICATION_STATUS_FORBID;
        }
        if(pass){
            verification.setStatus(VerificationStatus.PASS.getCode());
            ReturnObject<User> userRetObj = userDao.findUserById(verification.getUserId());
            if(userRetObj.getData() == null){
                return userRetObj.getCode();
            }
            User user = userRetObj.getData();
            user.setStudentVerify((byte)1);
            user.setSignature(user.createSignature());
            ResponseCode code = userDao.updateUserInfo(user);
            if(code != ResponseCode.OK){
                return code;
            }
        }
        else{
            verification.setStatus(VerificationStatus.FAILED.getCode());
        }
        return verificationDao.updateVerificationInfo(verification);
    }

    /**
     * 用户上传图片
     * @author snow create 2021/04/17 16:12
     * @param userId 用户id
     * @param img 图片
     * @return 上传文件的文件名
     */
    public ReturnObject<String> uploadFile(Long userId, MultipartFile img){
        String imgMD5 = MD5.getFileMd5(img);
        System.out.println("MD5: " + imgMD5);
        ReturnObject<String> imgExist = imageDao.isImageHasExist(imgMD5);
        if(imgExist.getCode() != ResponseCode.IMG_NOT_EXIST){
            return imgExist;
        }
        String fileName = savaImgToDisk(img);
        if(fileName == null){
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        System.out.println("FileName: " + fileName);
        Image image = new Image(userId, imgMD5, fileName);
        return imageDao.insertImage(image);
    }

    /**
     * 保存图片至磁盘
     * @author snow create 2021/04/17 14:40
     * @param img 图片
     * @return 图片路径
     */
    public String savaImgToDisk(MultipartFile img){
        try {
            String realPath = imgPath + "/" + UUID.randomUUID().toString().replace("-", "")+img.getOriginalFilename().substring(img.getOriginalFilename().lastIndexOf("."));
            File dest = new File(realPath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdir();
            }
            img.transferTo(dest);
            return dest.getName();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取校园数据
     * @author snow create 2021/05/07 00:26
     * @return 数据/错误码
     */
    public ReturnObject getCampusData(){
        CampusRetVo campusList = staticDao.getCampus();
        if(campusList == null){
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        }
        else{
            for(Campus campus : campusList.getCampusList()){
                campus.setBuilding(null);
            }
            return new ReturnObject(campusList);
        }
    }

    /**
     * 根据校园id获取校园宿舍信息
     * @author snow create 2021/05/07 00:31
     * @param campusId
     * @return
     */
    public ReturnObject<Campus> getCampusDataById(Integer campusId){
        CampusRetVo campusList = staticDao.getCampus();
        if(campusList == null){
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        else{
            for(Campus campus : campusList.getCampusList()){
                if(campusId.equals(campus.getId())){
                    return new ReturnObject<>(campus);
                }
            }
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOT_EXIST);
        }
    }

    /**
     * 获取快递公司数据
     * @author snow create 2021/05/07 00:27
     * @return 数据/错误码
     */
    public ReturnObject getCompanyData(){
        CompanyRetVo company = staticDao.getCompanies();
        if(company == null){
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        }
        else{
            return new ReturnObject(company);
        }
    }

    /**
     * 根据校区id获取地址数据
     * @author snow create 2021/05/06 20:52
     *            modified 2021/05/07 09:55
     * @param campusId 校区id
     * @return 数据/错误码
     */
    public ReturnObject getAddressData(Integer campusId){
        AddressRetVo address = staticDao.getAddresses(campusId);
        if(address == null){
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        }
        else{
            return new ReturnObject(address);
        }
    }

}
