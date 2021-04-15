package Privilege.model.bo;

import Core.model.VoObject;
import Core.util.AES;
import Core.util.Common;
import Core.util.SHA256;
import Privilege.model.po.AdminPo;
import Privilege.model.vo.AdminBasicInfoVo;
import Privilege.model.vo.AdminRetVo;
import Privilege.model.vo.AdminVo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author snow create 2021/01/18 11:45
 */
@Data
public class Admin implements VoObject, Serializable {
    public static String AES_PASS = "CompuOrg2021/01/17";

    protected Long id;
    protected Byte gender;
    private Byte role;
    protected String email;
    private Byte emailVerify;
    protected String mobile;
    private Byte mobileVerify;
    protected String userNo;
    private String userName;
    protected String password;
    protected String realName;
    private String signature;

    public Admin(AdminPo adminPo){
        this.id = adminPo.getId();
        this.role = adminPo.getRole();
        this.email = adminPo.getEmail();
        this.mobile = adminPo.getMobile();
        this.gender = adminPo.getGender();
        this.userName = adminPo.getUserName();
        this.password = adminPo.getPassword();
        this.realName = adminPo.getRealName();
        this.emailVerify = adminPo.getEmailVerify();
        this.mobileVerify = adminPo.getMobileVerify();
        this.signature = adminPo.getSignature();
    }

    public Admin(AdminVo adminVo){
        this.role = adminVo.getRole();
        this.gender = adminVo.getGender();
        this.userName = adminVo.getUserName();
        this.realName = adminVo.getRealName();
        this.email = AES.encrypt(adminVo.getEmail(), AES_PASS);
        this.mobile = AES.encrypt(adminVo.getMobile(), AES_PASS);
        this.password = AES.encrypt(adminVo.getPassword(), AES_PASS);
        this.emailVerify = (byte)1;
        this.mobileVerify = (byte)0;
        this.signature = createSignature();
    }

    public AdminPo createAdminPo(){
        AdminPo adminPo = new AdminPo();
        adminPo.setId(this.id);
        adminPo.setRole(this.role);
        adminPo.setEmail(this.email);
        adminPo.setMobile(this.mobile);
        adminPo.setGender(this.gender);
        adminPo.setUserName(this.userName);
        adminPo.setPassword(this.password);
        adminPo.setRealName(this.realName);
        adminPo.setSignature(this.signature);
        adminPo.setEmailVerify(this.emailVerify);
        adminPo.setMobileVerify(this.mobileVerify);
        return adminPo;
    }

    public String getDecryptEmail(){
        if(this.email != null) {
            return AES.decrypt(this.email, AES_PASS);
        }
        return null;
    }

    public String getDecryptMobile(){
        if(this.mobile != null) {
            return AES.decrypt(this.mobile, AES_PASS);
        }
        return null;
    }

    public void setGender(String gender){
        if(gender.equals("男")){
            this.gender = (byte)1;
        }
        else if(gender.equals("女")){
            this.gender = (byte)0;
        }
    }

    /**
     * 通过userBasicInfo中非空的属性更新属性值
     * @author snow create 2021/01/23 13:53
     * @param adminBasicInfoVo
     */
    public void updateInfo(AdminBasicInfoVo adminBasicInfoVo){
        if(adminBasicInfoVo.getUserName() != null){
            this.userName = adminBasicInfoVo.getUserName();
        }
        if(adminBasicInfoVo.getMobile() != null){
            this.mobile = AES.encrypt(adminBasicInfoVo.getMobile(), AES_PASS);
        }
        if(adminBasicInfoVo.getGender() != null){
            setGender(adminBasicInfoVo.getGender());
        }
        if(adminBasicInfoVo.getRealName() != null){
            this.realName = adminBasicInfoVo.getRealName();
        }
        this.signature = createSignature();
    }

    /**
     * 生成签名
     * @author snow create 2021/01/19 00:25
     * @return
     */
    public String createSignature(){
        StringBuilder signature = Common.concatString("-", this.userName, this.password, this.role.toString(), this.email, this.mobile);
        return SHA256.getSHA256(signature.toString());
    }

    /**
     * 判断签名是否被篡改
     * @author snow create 2021/01/19 00:26
     * @return
     */
    public Boolean isSignatureBeenModify(){
        if(this.signature.equals(createSignature())){
            return false;
        }
        return true;
    }

    public Boolean authentic(){
        return !isSignatureBeenModify();
    }

    @Override
    public Object createVo() {
        return new AdminRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
