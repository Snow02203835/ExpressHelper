package Privilege.service;

import Core.model.VoObject;
import Core.util.*;
import Privilege.dao.AdminDao;
import Privilege.model.bo.Admin;
import Privilege.model.po.AdminPo;
import Privilege.model.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private  static  final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Value("${Privilege.admin.login.jwtExpire}")
    private Integer jwtExpireTime;

    @Value("${Privilege.admin.departId}")
    private Long adminDepartId;

    private final String platformName = "【随便取管理平台】";
    private final String registrationTitle = "【随便取管理平台】注册验证通知";
    private final String verifyEmailTitle = "【随便取管理平台】邮箱验证通知";
    private final String resetPasswordEmailTitle = "【随便取管理平台】重置密码通知";

    @Autowired
    private AdminDao adminDao;

    /**
     * 管理员登录
     * @author snow create 2021/04/15 13:23
     * @param loginVo
     * @return
     */
    public ReturnObject adminLogin(LoginVo loginVo){
        ReturnObject<Admin> retObj = adminDao.findAdminByUserName(loginVo.getUserName());
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = retObj.getData();
        if(admin.isSignatureBeenModify()){
            return new ReturnObject<>(ResponseCode.RESOURCE_FALSIFY);
        }
        if(admin.getEmailVerify() == (byte)0){
            return new ReturnObject<>(ResponseCode.EMAIL_NOT_VERIFIED);
        }
        String password = AES.encrypt(loginVo.getPassword(), Admin.AES_PASS);
        if(!password.equals(admin.getPassword())){
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
        }
        String jwt = new JwtHelper().createToken(admin.getId(), Byte.toUnsignedLong(admin.getRole()), jwtExpireTime);
        return new ReturnObject<>(jwt);
    }

    /**
     * 管理员新建管理员
     * @author snow create 2021/04/15 13:38
     * @param adminVo
     * @return
     */
    public ReturnObject appendAdmin(AdminVo adminVo){
        return adminDao.insertAdmin(new Admin(adminVo));
    }

    /**
     * 管理员申请重置密码
     * @author snow create 2021/04/15 13:47
     * @param adminVo
     * @param ip
     * @return
     */
    public ReturnObject adminResetPassword(AdminPasswordVo adminVo, String ip){
        ReturnObject retObj = adminDao.findAdminByUserName(adminVo.getUserName());
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = (Admin) retObj.getData();
        logger.debug("Pass: " + adminVo.getEmail() + ", Store: " + admin.getDecryptEmail());
        if(!adminVo.getEmail().equals(admin.getDecryptEmail())){
            return new ReturnObject(ResponseCode.EMAIL_WRONG);
        }

        if(adminDao.isAllowRequestForVerifyCode(ip)) {
            //生成验证码
            String verifyCode = VerifyCode.generateVerifyCode(6);
            adminDao.putVerifyCodeIntoRedis(verifyCode, admin.getId().toString());
            String emailContent = "您正在" + platformName + "进行找回密码，您的验证码为：" + verifyCode + "，请于5分钟内完成验证！";
            return sendVerifyCode(resetPasswordEmailTitle, emailContent, adminVo.getEmail());
        }
        else{
            return new ReturnObject(ResponseCode.AUTH_USER_FORBIDDEN);
        }

    }

    /**
     * 管理员验证密码
     * @author snow create 2021/04/15 13:50
     * @param adminId
     * @param oldPassword
     * @return
     */
    public ReturnObject adminVerifyPassword(Long adminId, String oldPassword){
        ReturnObject<Admin> retObj = adminDao.findAdminById(adminId);
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = retObj.getData();
        if(AES.encrypt(oldPassword, Admin.AES_PASS).equals(admin.getPassword())){
            String verifyCode = VerifyCode.generateVerifyCode(6);
            adminDao.putVerifyCodeIntoRedis(verifyCode, admin.getId().toString());
            return new ReturnObject(verifyCode);
        }
        else{
            return new ReturnObject(ResponseCode.AUTH_INVALID_ACCOUNT);
        }
    }

    /**
     * 管理员验证验证码
     * @author snow create 2021/04/15 13:51
     * @param verifyCodeVo
     * @return
     */
    public ReturnObject verifyCode(VerifyCodeVo verifyCodeVo){
        Long adminId = adminDao.getAdminIdByVerifyCode(verifyCodeVo.getVerifyCode());
        if(adminId == null){
            System.out.println("Can't find anything in redis with: " + verifyCodeVo.getVerifyCode());
            return new ReturnObject(ResponseCode.VERIFY_CODE_EXPIRE);
        }
        String verifyKey = VerifyCode.generateVerifyCode(6) + LocalDateTime.now();
        adminDao.putVerifyCodeIntoRedis(verifyKey, adminId.toString());
        adminDao.disableVerifyCodeAfterSuccessfullyModifyPassword(verifyCodeVo.getVerifyCode());
        return new ReturnObject(verifyKey);
    }

    /**
     * 管理员修改密码
     * @author snow create 2021/04/15 13:51
     * @param modifyPasswordVo
     * @return
     */
    public ReturnObject adminModifyPassword(AdminModifyPasswordVo modifyPasswordVo){
        Long adminId = adminDao.getAdminIdByVerifyCode(modifyPasswordVo.getVerifyCode());
        if(adminId == null){
            System.out.println("Can't find anything in redis with: " + modifyPasswordVo.getVerifyCode());
            return new ReturnObject(ResponseCode.VERIFY_CODE_EXPIRE);
        }
        ReturnObject<Admin> retObj= adminDao.findAdminById(adminId);
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = retObj.getData();
        String password = AES.encrypt(modifyPasswordVo.getPassword(), Admin.AES_PASS);
        if(password.equals(admin.getPassword())){
            return new ReturnObject(ResponseCode.PASSWORD_SAME);
        }
        admin.setPassword(password);
        admin.setSignature(admin.createSignature());
        adminDao.disableVerifyCodeAfterSuccessfullyModifyPassword(modifyPasswordVo.getVerifyCode());
        return adminDao.updateAdminInformation(admin);
    }

    /**
     * 管理员查找个人信息
     * @author snow create 2021/04/15 13:52
     * @param adminId
     * @return
     */
    public ReturnObject getBasicInformation(Long adminId){
        return adminDao.findAdminById(adminId);
    }

    /**
     * 管理员查看管理员信息
     * @author snow create 2021/04/15 13:52
     * @param departId
     * @param role
     * @param userName
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> getAdminsBasicInformation(Long departId, Byte role, String userName, Integer page, Integer pageSize){
        if(!adminDepartId.equals(departId)){
            return new ReturnObject(ResponseCode.AUTH_NOT_ALLOW);
        }
        PageHelper.startPage(page, pageSize);
        PageInfo<AdminPo> adminPo = adminDao.findAdminsInCondition(role, userName);
        if(adminPo == null){
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        List<VoObject> adminInfos = adminPo.getList().stream().map(Admin::new).filter(Admin::authentic).collect(Collectors.toList());

        PageInfo<VoObject> retObj = new PageInfo<>(adminInfos);
        retObj.setPages(adminPo.getPages());
        retObj.setPageNum(adminPo.getPageNum());
        retObj.setPageSize(adminPo.getPageSize());
        retObj.setTotal(adminPo.getTotal());

        return new ReturnObject<>(retObj);
    }

    /**
     * 管理员修改基础信息
     * @author snow create 2021/04/15 13:54
     * @param adminId
     * @param adminBasicInfoVo
     * @return
     */
    public ReturnObject adminModifyBasicInformation(Long adminId, AdminBasicInfoVo adminBasicInfoVo){
        ReturnObject<Admin> retObj = adminDao.findAdminById(adminId);
        if (retObj.getData() == null){
            return retObj;
        }
        Admin admin = retObj.getData();
        if(adminBasicInfoVo.getUserName() != null){
            if(adminBasicInfoVo.getUserName().equals(admin.getUserName())){
                return new ReturnObject(ResponseCode.USER_NAME_SAME);
            }
            if(adminDao.isUserNameAlreadyExist(adminBasicInfoVo.getUserName())) {
                return new ReturnObject(ResponseCode.USER_NAME_REGISTERED);
            }
        }
        if(adminBasicInfoVo.getMobile() != null){
            if(adminBasicInfoVo.getMobile().equals(admin.getDecryptMobile())){
                return new ReturnObject(ResponseCode.MOBILE_SAME);
            }
            if(adminDao.isMobileAlreadyExist(AES.encrypt(adminBasicInfoVo.getMobile(), Admin.AES_PASS))) {
                return new ReturnObject(ResponseCode.MOBILE_REGISTERED);
            }
        }
        admin.updateInfo(adminBasicInfoVo);
        return adminDao.updateAdminInformation(admin);
    }

    /**
     * 管理员验证旧邮箱
     * @author snow create 2021/04/15 13:55
     * @param adminId
     * @param ip
     * @return
     */
    public ReturnObject adminVerifyEmail(Long adminId, String ip){
        ReturnObject<Admin> retObj = adminDao.findAdminById(adminId);
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = retObj.getData();
        if(adminDao.isAllowRequestForVerifyCode(ip)) {
            //生成验证码
            logger.debug("Ok!");
            String verifyCode = VerifyCode.generateVerifyCode(6);
            logger.debug("VerifyCode: " + verifyCode);
            adminDao.putVerifyCodeIntoRedis(verifyCode, adminId.toString());
            String emailContent = "您正在" + platformName + "进行邮箱验证，您的验证码为：" + verifyCode + "，请于5分钟内完成验证！";
            logger.debug(emailContent);
            return sendVerifyCode(verifyEmailTitle, emailContent, admin.getDecryptEmail());
        }
        else{
            logger.debug("busy try!");
            return new ReturnObject(ResponseCode.AUTH_USER_FORBIDDEN);
        }
    }

    /**
     * 管理员验证邮箱
     * @author snow create 2021/04/15 13:56
     * @param adminId
     * @param email
     * @param ip
     * @return
     */
    public ReturnObject adminVerifyEmail(Long adminId, String email, String ip){
        if(adminDao.isAllowRequestForVerifyCode(ip)) {
            //生成验证码
            logger.debug("Ok!");
            String verifyCode = VerifyCode.generateVerifyCode(6);
            logger.debug("VerifyCode: " + verifyCode);
            adminDao.putVerifyCodeIntoRedis(verifyCode, adminId.toString());
            String emailContent, title;
            emailContent = "您正在" + platformName + "进行邮箱验证，您的验证码为：" + verifyCode + "，请于5分钟内完成验证！";
            return sendVerifyCode(verifyEmailTitle, emailContent, email);
        }
        else{
            logger.debug("busy try!");
            return new ReturnObject(ResponseCode.AUTH_USER_FORBIDDEN);
        }

    }

    /**
     * 管理员修改邮箱
     * @author snow create 2021/04/15 13:58
     * @param adminVo
     * @return
     */
    public ReturnObject adminModifyEmail(Long adminId, AdminModifyEmailVo adminVo){
        Long redisValue = adminDao.getAdminIdByVerifyCode(adminVo.getVerifyCode());
        if(redisValue == null){
            System.out.println("Can't find anything in redis with: " + adminVo.getVerifyCode());
            return new ReturnObject(ResponseCode.VERIFY_CODE_EXPIRE);
        }
        ReturnObject<Admin> retObj= adminDao.findAdminById(adminId);
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = retObj.getData();
        String email = AES.encrypt(adminVo.getEmail(), Admin.AES_PASS);
        if(email.equals(admin.getEmail())){
            return new ReturnObject(ResponseCode.EMAIL_SAME);
        }
        if(adminDao.isEmailAlreadyExist(email)){
            return new ReturnObject(ResponseCode.EMAIL_REGISTERED);
        }
        admin.setEmail(email);
        admin.setSignature(admin.createSignature());
        adminDao.disableVerifyCodeAfterSuccessfullyModifyPassword(adminVo.getVerifyCode());
        return adminDao.updateAdminInformation(admin);
    }

    /**
     * 发送验证码
     * @author snow create 2021/04/15 13:57
     * @param title
     * @param content
     * @param toEmailAddress
     * @return
     */
    public ReturnObject sendVerifyCode(String title, String content, String toEmailAddress){
        try{

            //发送邮件
            SendEmail.sendEmail(toEmailAddress, title, content);
            return new ReturnObject(ResponseCode.OK);
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
    }
}
