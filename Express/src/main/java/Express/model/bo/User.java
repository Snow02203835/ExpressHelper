package Express.model.bo;

import Core.model.VoObject;
import Core.util.AES;
import Core.util.Common;
import Core.util.SHA256;
import Express.model.po.UserPo;
import Express.model.vo.UserInfoVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author snow create 2021/04/27 19:51
 *            modified 2021/04/28 09:00
 *            modified 2021/05/23 20:00
 */
@Data
public class User implements VoObject, Serializable {

    private final static String userKey = "express-help-2021-04-27-19-55";
    private final Integer fullCredit = 100;
    private Long id;
    private String name;
    private String openId;
    private Integer credit;
    private Integer succeed;
    private String mobile;
    private String address;
    private String studentNumber;
    private Byte studentVerify;
    private String signature;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public User(String openId){
        this.succeed = 0;
        this.credit = fullCredit;
        this.openId = openId;
        this.studentVerify = (byte)0;
        this.signature = createSignature();
    }

    public User(UserPo userPo){
        this.id = userPo.getId();
        this.name = userPo.getRealName();
        this.openId = userPo.getOpenId();
        this.credit = userPo.getCredit();
        this.mobile = userPo.getMobile();
        this.address = userPo.getAddress();
        this.succeed = userPo.getSucceed();
        this.studentNumber = userPo.getStudentNumber();
        this.studentVerify = userPo.getStudentVerify();
        this.signature = userPo.getSignature();
        this.gmtCreate = userPo.getGmtCreate();
        this.gmtModified = userPo.getGmtModified();
    }

    public UserPo createPo(){
        UserPo userPo = new UserPo();
        userPo.setId(this.id);
        userPo.setRealName(this.name);
        userPo.setOpenId(this.openId);
        userPo.setCredit(this.credit);
        userPo.setMobile(this.mobile);
        userPo.setAddress(this.address);
        userPo.setSucceed(this.succeed);
        userPo.setStudentNumber(this.studentNumber);
        userPo.setStudentVerify(this.studentVerify);
        userPo.setSignature(this.signature);
        userPo.setGmtCreate(this.gmtCreate);
        userPo.setGmtModified(this.gmtModified);
        return userPo;
    }

    public void updateInfoSelective(UserInfoVo userInfoVo){
        if(userInfoVo.getName() != null && !userInfoVo.getName().isBlank()){
            this.name = userInfoVo.getName();
        }
        if(userInfoVo.getMobile() != null && !userInfoVo.getMobile().isBlank()){
            setMobile(userInfoVo.getMobile());
        }
        if(userInfoVo.getAddress() != null && !userInfoVo.getAddress().isBlank()){
            setAddress(userInfoVo.getAddress());
        }
        if(userInfoVo.getStudentNumber() != null && !userInfoVo.getStudentNumber().isBlank()){
            this.studentNumber = userInfoVo.getStudentNumber();
        }
        this.signature = createSignature();
    }

    public String getDecryptMobile(){
        return AES.decrypt(this.mobile, userKey);
    }

    public void setMobile(String mobile){
        this.mobile = AES.encrypt(mobile, userKey);
    }

    public String getDecryptAddress(){
        return AES.decrypt(this.address, userKey);
    }

    public void setAddress(String address){
        this.address = AES.encrypt(address, userKey);
    }

    public void decreaseCredit(Integer credit){
        if (credit > this.credit){
            this.credit = 0;
        }
        else{
            this.credit -= credit;
        }
        if(this.credit > fullCredit){
            this.credit = fullCredit;
        }
        this.signature = createSignature();
    }

    public void successfullyDeliverPackage(){
        this.succeed++;
        if(this.succeed%10 == 0){
            decreaseCredit(-1);
        }
    }

    /**
     * 生成签名
     * @author snow create 2021/01/19 00:25
     *            modified 2021/04/28 08:48
     *            modified 2021/05/23 19:58
     * @return 签名
     */
    public String createSignature(){
        StringBuilder signature = Common.concatString("-", this.openId, this.name,
                this.credit.toString(), this.succeed.toString(), this.mobile,
                this.address, this.studentNumber, this.studentVerify.toString());
        return SHA256.getSHA256(signature.toString());
    }

    /**
     * 判断签名是否被篡改
     * @author snow create 2021/01/19 00:26
     *            modified 2021/05/23 20:00
     * @return bool
     */
    public Boolean isSignatureBeenModify(){
        return !this.signature.equals(createSignature());
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
