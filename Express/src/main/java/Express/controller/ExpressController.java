package Express.controller;

import Core.annotation.Depart;
import Core.util.Common;
import Core.util.ReturnObject;
import Core.annotation.Audit;
import Core.annotation.LoginUser;
import Express.model.vo.BillVo;
import Express.model.vo.DemandVo;
import Express.service.DemandService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

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

}
