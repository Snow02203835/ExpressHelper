package Express.controller;

import Core.annotation.Depart;
import Core.util.Common;
import Core.util.ResponseCode;
import Core.util.ResponseUtil;
import Core.util.ReturnObject;
import Core.annotation.Audit;
import Core.annotation.LoginUser;
import Express.model.vo.*;
import Express.service.DemandService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Api(value = "快递代取后端", tags = "express")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class ExpressController {

    @Autowired
    private DemandService demandService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 发布需求
     * @author snow create 2021/04/15 13:22
     * @param userId
     * @param demandVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "发布需求", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "DemandVo", name = "demandVo", value = "需求详细", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("demand")
    public Object releaseDemand(@ApiIgnore @LoginUser Long userId,
                             @Validated @RequestBody DemandVo demandVo,
                             BindingResult bindingResult){

        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        ReturnObject retObj = demandService.createDemand(userId, demandVo);

        if(retObj.getData() != null){
            return Common.getRetObject(retObj);
        }else{
            return Common.getNullRetObj(retObj, httpServletResponse);
        }

    }

    /**
     * 修改需求
     * @author snow create 2021/04/16 09:10
     * @param userId
     * @param departId
     * @param demandId
     * @param demandVo
     * @return
     */
    @ApiOperation(value = "修改需求", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "demandId", value = "需求id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "DemandVo", name = "demandVo", value = "需求详细", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("demand/{demandId}")
    public Object updateDemand(@ApiIgnore @LoginUser Long userId,
                               @ApiIgnore @Depart Long departId,
                               @PathVariable Long demandId,
                               @RequestBody DemandVo demandVo){
        return Common.decorateReturnObject(demandService.updateDemand(userId, departId, demandId, demandVo));
    }

    /**
     * 需求支付成功
     * @author snow create 2021/04/15 17:20
     *            modified 2021/04/15 19:23
     * @param userId
     * @param demandId
     * @param departId
     * @return
     */
    @ApiOperation(value = "需求支付成功", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "demandId", value = "需求id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "BillVo", name = "billVo", value = "支付信息", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("demand/payment/{demandId}")
    public Object paidForDemand(@ApiIgnore @LoginUser Long userId,
                                @ApiIgnore @Depart Long departId,
                                @PathVariable Long demandId,
                                @Validated @RequestBody BillVo billVo,
                                BindingResult bindingResult){

        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        ReturnObject retObj = demandService.userPaidForDemand(userId, departId, demandId, billVo);
        return Common.decorateReturnObject(retObj);
    }

    /**
     * 取消需求
     * @author snow create 2021/04/15 19:40
     * @param userId
     * @param departId
     * @param demandId
     * @return
     */
    @ApiOperation(value = "取消需求", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "demandId", value = "需求id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("demand/{demandId}/cancel")
    public Object cancelDemand(@ApiIgnore @LoginUser Long userId,
                               @ApiIgnore @Depart Long departId,
                               @PathVariable Long demandId){
        return Common.decorateReturnObject(demandService.cancelDemand(userId, departId, demandId));
    }

    /**
     * 删除需求记录
     * @author snow create 2021/04/15 15:48
     * @param userId
     * @param demandId
     * @return
     */
    @ApiOperation(value = "删除需求记录", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "demandId", value = "需求记录id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("demand/{demandId}")
    public Object deleteDemand(@ApiIgnore @LoginUser Long userId,
                             @PathVariable Long demandId){
        return Common.decorateReturnObject(demandService.deleteDemandLogically(userId, demandId));
    }

    /**
     * 获取需求记录
     * @author snow create 2021/04/15 00:33
     * @param userId
     * @param departId
     * @param demandId
     * @return
     */
    @ApiOperation(value = "获取需求记录", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "demandId", value = "需求记录id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("demand/{demandId}")
    public Object getDemandById(@ApiIgnore @LoginUser Long userId,
                                @ApiIgnore @Depart Long departId,
                                @PathVariable Long demandId){
        ReturnObject retObj = demandService.getDemandById(userId, departId, demandId);
        if(retObj.getData() == null){
            return Common.decorateReturnObject(retObj);
        }
        else {
            return Common.getRetObject(retObj);
        }
    }

    /**
     * 根据条件获取需求列表
     * @author snow create 2021/04/16 13:25
     *            modified 2021/04/17 00:48
     *            modified 2021/04/30 16:06
     * @param userId 用户id
     * @param departId 角色id
     * @param sponsorId 发布者id
     * @param type 类型
     * @param status 状态
     * @param deleted 逻辑删除是否可见
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param address 取件地址
     * @param destination 送达地址
     * @param startTime 开始时间
     * @param endTime 结果时间
     * @param page 页号
     * @param pageSize 页大小
     * @return 查询分页结果
     */
    @ApiOperation(value = "根据条件获取需求列表", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "sponsorId", value = "发布者id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "type", value = "需求类型", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "status", value = "需求状态", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "deleted", value = "逻辑删除需求是否可见", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "minPrice", value = "最低价格", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "maxPrice", value = "最高价格", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "address", value = "取件地址", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "destination", value = "送达地址", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "startTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", defaultValue = "1", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "页大小", defaultValue = "5", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("demand")
    public Object getDemands(@ApiIgnore @LoginUser Long userId,
                             @ApiIgnore @Depart Long departId,
                             @RequestParam(required = false) Long sponsorId,
                             @RequestParam(required = false) Byte type,
                             @RequestParam(required = false) Byte status,
                             @RequestParam(required = false) Byte deleted,
                             @RequestParam(required = false) Integer minPrice,
                             @RequestParam(required = false) Integer maxPrice,
                             @RequestParam(required = false) String address,
                             @RequestParam(required = false) String destination,
                             @RequestParam(required = false) String startTime,
                             @RequestParam(required = false) String endTime,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "5") Integer pageSize){
        if(page < 1 || pageSize < 0){
            return Common.getNullRetObj(new ReturnObject(ResponseCode.FIELD_NOT_VALID), httpServletResponse);
        }
        System.out.println("departId: " + departId + ", SponsorId: " + sponsorId + ", type: " + type + ", status: " +status);
        System.out.println("deleted: " + deleted + ", minPrice: " + minPrice + ", maxPrice: " + maxPrice + ", address: " + address);
        System.out.println("destination: " + destination + ", startTime: " + startTime + ", endTime: " + endTime + ", page: " + page + ", pageSize: " + pageSize);
        LocalDateTime start = null, end = null;
        try {
            if(startTime != null) {
                start = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if(endTime != null){
                end = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return Common.getNullRetObj(new ReturnObject(ResponseCode.FIELD_NOT_VALID), httpServletResponse);
        }
        return Common.getPageRetObject(demandService.getDemandsWithCondition(userId, departId, sponsorId, type, status,
                deleted, minPrice, maxPrice, address, destination, start, end, page, pageSize));
    }

    /**
     * 用户接单
     * @author snow create 2021/04/15 13:22
     * @param userId
     * @param demandId
     * @return
     */
    @ApiOperation(value = "用户接单", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "demandId", value = "需求id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("order/{demandId}")
    public Object pickUpDemand(@ApiIgnore @LoginUser Long userId,
                               @PathVariable Long demandId){

        ReturnObject retObj = demandService.pickUpDemand(userId, demandId);

        if(retObj.getData() != null){
            return Common.getRetObject(retObj);
        }else{
            return Common.getNullRetObj(retObj, httpServletResponse);
        }

    }

    /**
     * 用户取消订单
     * @author snow create 2021/04/16 08:31
     * @param userId
     * @param departId
     * @param orderId
     * @return
     */
    @ApiOperation(value = "用户取消订单", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "orderId", value = "订单id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("order/{orderId}/cancel")
    public Object cancelOrder(@ApiIgnore @LoginUser Long userId,
                              @ApiIgnore @Depart Long departId,
                              @PathVariable Long orderId){
        return Common.decorateReturnObject(demandService.cancelOrder(userId, departId, orderId));
    }

    /**
     * 用户删除订单
     * @author snow create 2021/04/16 08:54
     * @param userId
     * @param orderId
     * @return
     */
    @ApiOperation(value = "用户删除订单", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "orderId", value = "订单id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("order/{orderId}")
    public Object deleteOrder(@ApiIgnore @LoginUser Long userId,
                              @PathVariable Long orderId){
        return Common.decorateReturnObject(demandService.deleteOrderLogically(userId, orderId));
    }

    /**
     * 用户取件/送达
     * @author snow create 2021/04/15 20:03
     * @param userId
     * @param orderId
     * @return
     */
    @ApiOperation(value = "用户取件/送达", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "orderId", value = "订单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "URLVo", name = "urlVo", value = "取件图片地址", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("order/{orderId}")
    public Object pickUpPackage(@ApiIgnore @LoginUser Long userId,
                                @PathVariable Long orderId,
                                @Validated @RequestBody URLVo urlVo,
                                BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }
        return Common.decorateReturnObject(demandService.updateOrderStatusWithURL(userId, orderId, urlVo.getUrl()));
    }

    /**
     * 根据id获取订单
     * @author snow create 2021/04/15 01:02
     * @param userId
     * @param departId
     * @param orderId
     * @return
     */
    @ApiOperation(value = "根据id获取订单", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "orderId", value = "订单id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("order/{orderId}")
    public Object getOrderById(@ApiIgnore @LoginUser Long userId,
                               @ApiIgnore @Depart Long departId,
                               @PathVariable Long orderId){
        ReturnObject retObj = demandService.getOrderById(userId, departId, orderId);
        if(retObj.getData() == null){
            return Common.decorateReturnObject(retObj);
        }
        else {
            return Common.getRetObject(retObj);
        }
    }

    /**
     * 根据条件获取订单列表
     * @author snow create 2021/04/17 00:42
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
    @ApiOperation(value = "根据条件获取订单列表", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "receiverId", value = "接单者id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "status", value = "订单状态", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "deleted", value = "逻辑删除订单是否可见", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "startTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", defaultValue = "1", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "页大小", defaultValue = "5", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("order")
    public Object getOrders(@ApiIgnore @LoginUser Long userId,
                            @ApiIgnore @Depart Long departId,
                            @RequestParam(required = false) Long receiverId,
                            @RequestParam(required = false) Byte status,
                            @RequestParam(required = false) Byte deleted,
                            @RequestParam(required = false) String startTime,
                            @RequestParam(required = false) String endTime,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "5") Integer pageSize){
        if(page < 1 || pageSize < 0){
            return Common.getNullRetObj(new ReturnObject(ResponseCode.FIELD_NOT_VALID), httpServletResponse);
        }
        System.out.println("userId: " + userId + ", departId: " + departId + ", status: " +status + ", deleted: " + deleted);
        System.out.println("startTime: " + startTime + ", endTime: " + endTime + ", page: " + page + ", pageSize: " + pageSize);
        LocalDateTime start = null, end = null;
        try {
            if(startTime != null) {
                start = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if(endTime != null){
                end = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return Common.getNullRetObj(new ReturnObject(ResponseCode.FIELD_NOT_VALID), httpServletResponse);
        }
        return Common.getPageRetObject(demandService.getOrdersWithCondition(userId, departId, receiverId, status, deleted, start, end, page, pageSize));
    }

    /**
     * 用户反馈
     * @author snow create 2021/04/19 01:39
     *            modified 2021/04/19 10:37
     *            modified 2021/04/19 11:23
     *            modified 2021/04/20 19:32
     * @param userId 用户id
     * @param feedbackVo 反馈信息
     * @param bindingResult 校验信息
     * @return 插入结果
     */
    @ApiOperation(value = "用户反馈", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "FeedbackVo", name = "feedbackVo", value = "反馈内容", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("order/feedback")
    public Object userFeedback(@ApiIgnore @LoginUser Long userId,
                               @Validated @RequestBody FeedbackVo feedbackVo,
                               BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }
        ReturnObject retObj = demandService.userFeedback(userId, feedbackVo);
        if(retObj.getData() == null){
            return Common.decorateReturnObject(retObj);
        }
        else {
            return Common.getRetObject(retObj);
        }
    }

    /**
     * 用户更新反馈内容
     * @author snow create 2021/04/19 13:19
     *            modified 2021/04/19 19:59
     * @param userId 用户id
     * @param feedbackId 反馈信息id
     * @param contentVo 新反馈内容
     * @return 操作结果
     */
    @ApiOperation(value = "用户更新反馈内容", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "feedbackId", value = "反馈id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "FeedbackVo", name = "contentVo", value = "新反馈内容", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("order/feedback/{feedbackId}")
    public Object userUpdateFeedbackContent(@ApiIgnore @LoginUser Long userId,
                                            @PathVariable Long feedbackId,
                                            @RequestBody FeedbackVo contentVo){
        return Common.decorateReturnObject(demandService.userUpdateFeedbackContent(userId, feedbackId,
                contentVo.getImg(), contentVo.getContent()));
    }

    /**
     * 用户取消反馈
     * @author snow create 2021/04/19 15:03
     * @param userId 用户id
     * @param feedbackId 反馈信息id
     * @return 操作结果
     */
    @ApiOperation(value = "用户取消反馈", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "feedbackId", value = "反馈id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("order/feedback/{feedbackId}/cancel")
    public Object userCancelFeedback(@ApiIgnore @LoginUser Long userId,
                                     @PathVariable Long feedbackId){
        return Common.decorateReturnObject(demandService.userCancelFeedback(userId, feedbackId));
    }

    /**
     * 用户删除反馈
     * @author snow create 2021/04/19 15:04
     * @param userId 用户id
     * @param feedbackId 反馈信息id
     * @return 操作结果
     */
    @ApiOperation(value = "用户删除反馈", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "feedbackId", value = "反馈id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("order/feedback/{feedbackId}")
    public Object userDeleteFeedback(@ApiIgnore @LoginUser Long userId,
                                     @PathVariable Long feedbackId){
        return Common.decorateReturnObject(demandService.userDeleteFeedback(userId, feedbackId));
    }

    /**
     * 管理员受理用户反馈
     * @author snow create 2021/04/19 15:06
     * @param feedbackId 反馈信息id
     * @return 操作结果
     */
    @ApiOperation(value = "管理员受理用户反馈", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "feedbackId", value = "反馈id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("order/feedback/{feedbackId}/handling")
    public Object adminHandlingFeedback(@PathVariable Long feedbackId){
        return Common.decorateReturnObject(demandService.adminHandlingFeedback(feedbackId));
    }

    /**
     * 管理员答复用户反馈
     * @author snow create 2021/04/19 15:09
     * @param feedbackId 反馈信息id
     * @param responseVo 反馈答复
     * @param bindingResult 校验
     * @return 操作结果
     */
    @ApiOperation(value = "管理员答复用户反馈", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "feedbackId", value = "反馈id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "FeedbackResponseVo", name = "responseVo", value = "反馈答复", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("order/feedback/{feedbackId}/response")
    public Object adminRespondFeedback(@PathVariable Long feedbackId,
                                       @RequestBody FeedbackResponseVo responseVo,
                                       BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }
        return Common.decorateReturnObject(demandService.adminRespondFeedback(feedbackId, responseVo.getResponse()));
    }

    /**
     * 获取用户反馈
     * @author snow create 2021/04/19 01:42
     *            modified 2021/04/19 10:38
     * @param userId 用户id
     * @param departId 角色id
     * @param feedbackId 反馈id
     * @return 反馈详细
     */
    @ApiOperation(value = "获取用户反馈", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "feedbackId", value = "反馈id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("order/feedback/{feedbackId}")
    public Object getUserFeedback(@ApiIgnore @LoginUser Long userId,
                                  @ApiIgnore @Depart Long departId,
                                  @PathVariable Long feedbackId){
        ReturnObject retObj = demandService.getUserFeedbackById(userId, departId, feedbackId);
        if(retObj.getData() == null){
            return Common.decorateReturnObject(retObj);
        }
        else {
            return Common.getRetObject(retObj);
        }
    }

    /**
     * 根据订单id获取用户反馈
     * @author snow create 2021/04/19 10:44
     * @param userId 用户id
     * @param departId 角色id
     * @param orderId 订单id
     * @return 反馈详细
     */
    @ApiOperation(value = "根据订单id获取用户反馈", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "orderId", value = "订单id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("order/{orderId}/feedback")
    public Object getUserFeedbackByOrderId(@ApiIgnore @LoginUser Long userId,
                                           @ApiIgnore @Depart Long departId,
                                           @PathVariable Long orderId){
        ReturnObject retObj = demandService.getUserFeedbackByOrderId(userId, departId, orderId);
        if(retObj.getData() == null){
            return Common.decorateReturnObject(retObj);
        }
        else {
            return Common.getRetObject(retObj);
        }
    }

    /**
     * 根据条件查找用户反馈
     * @author snow create 2021/04/19 15:33
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
    @ApiOperation(value = "根据条件查找用户反馈", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "ownerId", value = "反馈所有者id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "type", value = "反馈类型", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "status", value = "反馈状态", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "deleted", value = "逻辑删除反馈是否可见", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "content", value = "反馈内容", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "startTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", defaultValue = "1", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "页大小", defaultValue = "5", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("order/feedback")
    public Object getUserFeedbackWithCondition(@ApiIgnore @LoginUser Long userId,
                                               @ApiIgnore @Depart Long departId,
                                               @RequestParam(required = false) Long ownerId,
                                               @RequestParam(required = false) Byte type,
                                               @RequestParam(required = false) Byte status,
                                               @RequestParam(required = false) Byte deleted,
                                               @RequestParam(required = false) String content,
                                               @RequestParam(required = false) String startTime,
                                               @RequestParam(required = false) String endTime,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "5") Integer pageSize){
        if(page < 1 || pageSize < 0){
            return Common.getNullRetObj(new ReturnObject(ResponseCode.FIELD_NOT_VALID), httpServletResponse);
        }
        System.out.println("departId: " + departId + ", ownerId: " + ownerId + ", type: " + type + ", status: " +status);
        System.out.println("deleted: " + deleted + ", content: " + content + ", startTime: " + startTime);
        System.out.println("endTime: " + endTime + ", page: " + page + ", pageSize: " + pageSize);
        LocalDateTime start = null, end = null;
        try {
            if(startTime != null) {
                start = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if(endTime != null){
                end = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return Common.getNullRetObj(new ReturnObject(ResponseCode.FIELD_NOT_VALID), httpServletResponse);
        }
        return Common.getPageRetObject(demandService.getUserFeedbackWithCondition(userId, departId,
                ownerId, type, status, deleted, content, start, end, page, pageSize));
    }

    /**
     * 用户上传图片
     * @author snow create 2021/04/17 16:14
     * @param userId 用户id
     * @param img 图片文件
     * @return 操作结果
     */
    @ApiOperation(value = "上传图片", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "img", value = "图片", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("image")
    public Object uploadFile(@ApiIgnore @LoginUser Long userId,
                             @RequestParam MultipartFile img){
        System.out.println("UserId: " + userId + ", FileSize: " + img.getSize() + ", FileName: " + img.getOriginalFilename());
        ReturnObject<String> returnObject = demandService.uploadFile(userId, img);
        if(returnObject.getData() == null){
            return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }else{
            return ResponseUtil.ok(returnObject.getData());
        }
    }

    /**
     * 用户登录
     * @author RLY create 2021/04/26 20:00
     *      snow modified 2021/04/27 21:11
     * @param code 小程序传来的js_code
     * @return UserLoginRetVo 包含token的对象
     */
    @PostMapping("user/login")
    public Object userLogin (@RequestParam("code") String code){
        ReturnObject retObj = demandService.userLogin(code);
        if(retObj.getData() != null){
            return Common.getRetObject(retObj);
        }
        else{
            return Common.decorateReturnObject(retObj);
        }
    }

    /**
     * 用户更新自身信息
     * @author snow create 2021/04/27 21:44
     *            modified 2021/04/29 14:33
     * @param userId 用户id
     * @param userInfo 用户信息
     * @return 操作结果
     */
    @ApiOperation(value = "用户更新自身信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "UserInfoVo", name = "userInfo", value = "用户信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("user")
    public Object userUpdateSelfInfo(@ApiIgnore @LoginUser Long userId,
                                     @RequestBody UserInfoVo userInfo){
        return Common.decorateReturnObject(new ReturnObject(demandService.userUpdateSelfInfo(userId, userInfo)));
    }

    /**
     * 用户提交学生认证
     * @author snow create 2021/04/29 10:21
     *            modified 2021/04/29 13:53
     * @param userId 用户id
     * @param verificationVo 认证信息
     * @param bindingResult 校验信息
     * @return 提交结果
     */
    @ApiOperation(value = "用户提交学生认证", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VerificationVo", name = "verificationVo", value = "用户信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("user/verification")
    public Object userCommitVerification (@ApiIgnore @LoginUser Long userId,
                                          @Validated @RequestBody VerificationVo verificationVo,
                                          BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }
        ReturnObject retObj = demandService.userCommitVerification(userId, verificationVo);
        if(retObj.getData() != null){
            return Common.getRetObject(retObj);
        }
        else{
            return Common.decorateReturnObject(retObj);
        }
    }

    /**
     * 用户修改学生认证信息
     * @author snow create 2021/04/29 14:12
     * @param userId 用户id
     * @param verificationId 认证id
     * @param verificationVo 认证信息
     * @return 操作结果
     */
    @ApiOperation(value = "用户修改学生认证信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VerificationVo", name = "verificationVo", value = "用户信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("user/verification/{verificationId}")
    public Object userAlterVerification(@ApiIgnore @LoginUser Long userId,
                                        @PathVariable Long verificationId,
                                        @RequestBody VerificationVo verificationVo){
        return Common.decorateReturnObject(new ReturnObject(
                demandService.userAlterVerificationInfo(userId, verificationId, verificationVo)));
    }

    /**
     * 管理员审核学生认证
     * @author snow create 2021/04/29 10:51
     * @param verificationId 认证id
     * @param pass 是否通过
     * @return 操作结果
     */
    @ApiOperation(value = "用户提交学生认证", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "verificationId", value = "认证id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pass", value = "是否通过", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("admin/user/verification{verificationId}")
    public Object adminAuditVerification(@PathVariable Long verificationId, @RequestParam Byte pass){
        ResponseCode code = demandService.adminAuditUserVerification(verificationId, (pass == (byte)1));
        return Common.decorateReturnObject(new ReturnObject(code));
    }

}

