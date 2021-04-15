package Privilege.controller;

import Core.annotation.Audit;
import Core.annotation.Depart;
import Core.annotation.LoginUser;
import Core.util.*;
import Privilege.model.vo.*;
import Privilege.model.vo.PasswordVo;
import Privilege.model.vo.VerifyCodeVo;
import Privilege.service.AdminService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "快递代取项目后端", tags = "privilege")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/privilege", produces = "application/json;charset=UTF-8")
public class PrivilegeController {

    private  static  final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 管理员登录
     * @author snow create 2021/04/15 13:22
     * @param loginVo
     * @param bindingResult
     * @param httpServletResponse
     * @return
     */
    @ApiOperation(value = "管理员登录", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "LoginVo", name = "loginVo", value = "管理员名与密码", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 748, message = "Email未确认"),
    })
    @PostMapping("admin/login")
    public Object adminLogin(@Validated @RequestBody LoginVo loginVo,
                            BindingResult bindingResult,
                            HttpServletResponse httpServletResponse){

        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        ReturnObject<String> jwt = adminService.adminLogin(loginVo);

        if(jwt.getData() == null){
            return ResponseUtil.fail(jwt.getCode(), jwt.getErrmsg());
        }else{
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return ResponseUtil.ok(jwt.getData());
        }

    }

    /**
     * 管理员新建管理员
     * @author snow create 2021/04/15 13:40
     * @param adminVo
     * @param bindingResult
     * @param httpServletResponse
     * @return
     */
    @ApiOperation(value = "管理员新建管理员", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "UserVo", name = "userVo", value = "管理员信息", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功", response = AdminRetVo.class),
            @ApiResponse(code = 705, message = "无权限访问"),
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 733, message = "电话已被注册"),
            @ApiResponse(code = 736, message = "管理员名已被注册"),
    })
    @Audit
    @PostMapping("admin")
    public Object appendAdmin(@Validated @RequestBody AdminVo adminVo,
                              BindingResult bindingResult,
                              HttpServletResponse httpServletResponse){

        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        ReturnObject retObj = adminService.appendAdmin(adminVo);

        if(retObj.getData() == null){
            return Common.getNullRetObj(retObj, httpServletResponse);
        }else{
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(retObj);
        }
    }
    
    /**
     * 管理员找回密码
     * @author snow create 2021/04/15 13:42
     * @param adminVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员找回密码", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "UserPasswordVo", name = "adminVo", value = "管理员验证身份信息", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 702, message = "用户被禁止登录"),
            @ApiResponse(code = 745, message = "与系统预留的邮箱不一致"),
    })
    @PutMapping("admin/password/reset")
    public Object adminResetPassword(@Validated @RequestBody AdminPasswordVo adminVo,
                                    BindingResult bindingResult,
                                    HttpServletRequest httpServletRequest){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        String ip = IpUtil.getIpAddr(httpServletRequest);
        return Common.decorateReturnObject(adminService.adminResetPassword(adminVo, ip));

    }

    /**
     * 管理员验证密码
     * @author snow create 2021/04/15 14:11
     * @param adminId
     * @param passwordVo
     * @return
     */
    @ApiOperation(value = "管理员验证密码", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "PasswordVo", name = "passwordVo", value = "旧密码", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 700, message = "管理员名不存在或者密码错误"),
    })
    @Audit
    @PutMapping("admin/password/verify")
    public Object userVerifyPassword(@ApiIgnore @LoginUser Long adminId,
                                     @Validated @RequestBody PasswordVo passwordVo,
                                     BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }
        ReturnObject retObj = adminService.adminVerifyPassword(adminId, passwordVo.getPassword());
        if(retObj.getData() == null){
            return ResponseUtil.fail(retObj.getCode(), retObj.getErrmsg());
        }else{
            return ResponseUtil.ok(retObj.getData());
        }
    }

    /**
     * 管理员修改密码
     * @author snow create 2021/04/15 14:10
     * @param modifyPasswordVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员修改密码", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "AdminModifyPasswordVo", name = "modifyPasswordVo", value = "修改密码对象", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 741, message = "新密码不能与旧密码相同"),
            @ApiResponse(code = 750, message = "验证码不正确或已过期"),
    })
    @PutMapping("admin/password")
    public Object userModifyPassword(@Validated @RequestBody AdminModifyPasswordVo modifyPasswordVo,
                                     BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        return Common.decorateReturnObject(adminService.adminModifyPassword(modifyPasswordVo));
    }

    /**
     * 管理员查看个人信息
     * @author snow create 2021/04/15 14:10
     * @param userId
     * @return
     */
    @ApiOperation(value = "管理员查看个人信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("admin")
    public Object userGetBasicInformation(@ApiIgnore @LoginUser Long userId){
        ReturnObject retObj = adminService.getBasicInformation(userId);
        if(retObj.getData() == null){
            return Common.decorateReturnObject(retObj);
        }
        else{
            return Common.getRetObject(retObj);
        }
    }

    /**
     * 管理员查看系统管理员
     * @author snow create 2021/04/15 14:09
     * @param departId
     * @param role
     * @param userName
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "管理员查看系统管理员", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "role", value = "管理员角色", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "userName", value = "用户名名", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", defaultValue = "1", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "页大小", defaultValue = "5", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("admin/informations")
    public Object getAdminInformation(@ApiIgnore @Depart Long departId,
                                          @RequestParam(required = false) Byte role,
                                          @RequestParam(required = false) String userName,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "5") Integer pageSize){
        if(page < 1 || pageSize < 0){
            return Common.getNullRetObj(new ReturnObject(ResponseCode.FIELD_NOT_VALID), httpServletResponse);
        }
        return Common.getPageRetObject(adminService.getAdminsBasicInformation(departId, role, userName, page, pageSize));
    }

    /**
     * 管理员修改基本信息
     * @author snow create 2021/04/15 14:07
     * @param adminId
     * @param adminBasicInfoVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员修改基本信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AdminBasicInfoVo", name = "adminBasicInfoVo", value = "修改信息对象", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 700, message = "管理员名不存在或者密码错误"),
            @ApiResponse(code = 731, message = "用户名名已被注册"),
            @ApiResponse(code = 733, message = "电话已被注册"),
    })
    @Audit
    @PutMapping("admin/information")
    public Object studentModifyBasicInformation(@ApiIgnore @LoginUser Long adminId,
                                                @Validated @RequestBody AdminBasicInfoVo adminBasicInfoVo,
                                                BindingResult bindingResult){
        logger.debug("AdminId: " + adminId + "userInfo: " + adminBasicInfoVo.toString());
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        return Common.decorateReturnObject(adminService.adminModifyBasicInformation(adminId, adminBasicInfoVo));
    }

    /**
     * 管理员注册时验证邮箱
     * @author snow create 2021/04/15 14:07
     * @param emailVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员注册时验证邮箱", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "EmailVo", name = "emailVo", value = "邮箱", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("admin/email/verify")
    public Object verifyEmail(@ApiIgnore @LoginUser Long adminId,
                                  @Validated @RequestBody EmailVo emailVo,
                                  BindingResult bindingResult,
                                  HttpServletRequest httpServletRequest){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }
        String ip = IpUtil.getIpAddr(httpServletRequest);
        return Common.decorateReturnObject(adminService.adminVerifyEmail(adminId, emailVo.getEmail(), ip));
    }

    /**
     * 管理员验证旧邮箱
     * @author snow create 2021/04/15 14:06
     * @param adminId
     * @param httpServletRequest
     * @return
     */
    @ApiOperation(value = "管理员验证旧邮箱", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 700, message = "管理员不存在或者密码错误"),
    })
    @Audit
    @GetMapping("admin/email/verify")
    public Object verifyOldEmail(@ApiIgnore @LoginUser Long adminId,
                                  HttpServletRequest httpServletRequest){
        logger.debug("UserId: " + adminId);
        String ip = IpUtil.getIpAddr(httpServletRequest);

        return Common.decorateReturnObject(adminService.adminVerifyEmail(adminId, ip));
    }

    /**
     * 管理员验证新邮箱
     * @author snow create 2021/04/15 14:03
     * @param adminId
     * @param emailVo
     * @param bindingResult
     * @param httpServletRequest
     * @return
     */
    @ApiOperation(value = "管理员验证新邮箱", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "EmailVo", name = "emailVo", value = "邮箱", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 700, message = "管理员名不存在或者密码错误"),
    })
    @Audit
    @PutMapping("admin/email/verify")
    public Object userVerifyNewEmail(@ApiIgnore @LoginUser Long adminId,
                                     @Validated @RequestBody EmailVo emailVo,
                                     BindingResult bindingResult,
                                     HttpServletRequest httpServletRequest){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }
        String ip = IpUtil.getIpAddr(httpServletRequest);
        return Common.decorateReturnObject(adminService.adminVerifyEmail(adminId, emailVo.getEmail(), ip));

    }

    /**
     * 管理员修改邮箱
     * @author snow create 2021/04/15 14:04
     * @param adminId
     * @param adminVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员修改邮箱", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AdminModifyEmailVo", name = "adminVo", value = "修改邮箱对象", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 700, message = "管理员名不存在或者密码错误"),
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 750, message = "验证码不正确或已过期"),
    })
    @Audit
    @PutMapping("admin/email")
    public Object userModifyEmail(@LoginUser @ApiIgnore Long adminId,
                                  @Validated @RequestBody AdminModifyEmailVo adminVo,
                                  BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        return Common.decorateReturnObject(adminService.adminModifyEmail(adminId, adminVo));
    }

    /**
     * 验证验证码
     * @author snow create 2021/04/15 14:04
     * @param verifyCode
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "验证验证码", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "VerifyCodeVo", name = "verifyCode", value = "管理员验证身份信息", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 702, message = "管理员被禁止登录"),
    })
    @PutMapping("verifyCode")
    public Object verifyCode(@Validated @RequestBody VerifyCodeVo verifyCode,
                                 BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }
        ReturnObject retObj = adminService.verifyCode(verifyCode);
        if(retObj.getData() == null){
            return ResponseUtil.fail(retObj.getCode(), retObj.getErrmsg());
        }
        else{
            return ResponseUtil.ok(retObj.getData());
        }

    }
}
