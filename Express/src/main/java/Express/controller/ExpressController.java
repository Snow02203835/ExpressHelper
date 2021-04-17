package Express.controller;

import Core.annotation.Depart;
import Core.util.Common;
import Core.util.ResponseCode;
import Core.util.ResponseUtil;
import Core.util.ReturnObject;
import Core.annotation.Audit;
import Core.annotation.LoginUser;
import Express.model.vo.BillVo;
import Express.model.vo.DemandVo;
import Express.model.vo.URLVo;
import Express.service.DemandService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public Object getDemands(@ApiIgnore @Depart Long departId,
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
        return Common.getPageRetObject(demandService.getDemandsWithCondition(departId, sponsorId, type, status,
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

}
