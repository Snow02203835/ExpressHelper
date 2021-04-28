package Express.model.vo;

import Core.model.VoObject;
import Express.model.bo.User;
import lombok.Data;

import java.io.Serializable;

/**
 * @author snow create 2021/04/27 20:55
 *            modified 2021/04/28 08:49
 */
@Data
public class UserLoginRetVo implements VoObject, Serializable {
    private String name;
    private String token;
    private String mobile;
    private String address;
    private String studentNumber;
    private Byte studentVerify;
    private Integer credit;

    public UserLoginRetVo(String token, User user){
        this.token = token;
        this.name = user.getName();
        this.credit = user.getCredit();
        this.mobile = user.getDecryptMobile();
        this.address = user.getDecryptAddress();
        this.studentNumber = user.getStudentNumber();
        this.studentVerify = user.getStudentVerify();
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

}
