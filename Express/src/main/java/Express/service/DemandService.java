package Express.service;

import Core.model.VoObject;
import Core.util.MD5;
import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Express.dao.DemandDao;
import Express.dao.FeedbackDao;
import Express.dao.ImageDao;
import Express.dao.OrderDao;
import Express.model.bo.*;
import Express.model.po.DemandPo;
import Express.model.po.FeedBackPo;
import Express.model.po.OrderPo;
import Express.model.vo.BillVo;
import Express.model.vo.DemandVo;
import Express.model.vo.FeedbackVo;
import Express.model.vo.OrderRetVo;
import Express.util.DemandStatus;
import Express.util.FeedbackStatus;
import Express.util.OrderStatus;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DemandService {

    private final Long userDepartId = -1L;

    @Autowired
    private DemandDao demandDao;
    @Autowired
    private FeedbackDao feedbackDao;
    @Autowired
    private ImageDao imageDao;
    @Autowired
    private OrderDao orderDao;

    @Value("${Express.img.path}")
    private String imgPath;

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
                if(DemandStatus.PICKED.getCode().equals(demand.getStatus())){
                    List<Order> orderList = orderDao.findOrderByDemandId(demandId);
                    if(orderList == null){
                        return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
                    }
                    Byte orderStatus = orderList.get(orderList.size()-1).getStatus();
                    if(OrderStatus.PICKED.getCode().equals(orderStatus)){
                        //credit
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
     * 根据条件查找需求
     * @author snow create 2021/04/16 11:08
     *            modified 2021/04/17 00:49
     * @param departId
     * @param sponsorId
     * @param type
     * @param status
     * @param deleted
     * @param minPrice
     * @param maxPrice
     * @param address
     * @param destination
     * @param startTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> getDemandsWithCondition(Long departId, Long sponsorId, Byte type, Byte status,
                                                                    Byte deleted, Integer minPrice, Integer maxPrice,
                                                                    String address, String destination,
                                                                    LocalDateTime startTime, LocalDateTime endTime,
                                                                    Integer page, Integer pageSize){
        if(userDepartId.equals(departId)){
            deleted = (byte)0;
        }
        PageHelper.startPage(page, pageSize);
        PageInfo<DemandPo> demandPoPageInfo = demandDao.findDemandsWithCondition(sponsorId, type, status, deleted,
                minPrice, maxPrice, address, destination, startTime, endTime);
        if(demandPoPageInfo == null){
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        List<VoObject> userInfos = demandPoPageInfo.getList().stream().map(Demand::new).collect(Collectors.toList());
        System.out.println(userInfos.toString());

        PageInfo<VoObject> retObj = new PageInfo<>(userInfos);
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

    /**
     * 根据条件查找订单
     * @author snow create 2021/04/17 00:39
     * @param userId
     * @param departId
     * @param receiverId
     * @param status
     * @param deleted
     * @param startTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
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
        List<VoObject> orderInfos = orderPoPageInfo.getList().stream().map(Order::new).collect(Collectors.toList());
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
     * @param userId 用户id
     * @param feedbackVo 反馈信息
     * @return 插入结果
     */
    public ReturnObject userFeedback(Long userId, FeedbackVo feedbackVo){
        Feedback feedback = new Feedback(userId, feedbackVo);
        return feedbackDao.insertFeedback(feedback);
    }

    /**
     * 用户更新反馈内容
     * @author snow create 2021/04/19 13:11
     *            modified 2021/04/20 19:58
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

}
