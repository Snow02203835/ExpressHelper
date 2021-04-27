package Express.model.vo;

import Core.model.VoObject;
import Express.model.bo.User;
import lombok.Data;

import java.io.Serializable;

/**
 * @author snow create 2021/04/27 20:55
 */
@Data
public class UserLoginRetVo implements VoObject, Serializable {
    private String token;
    private String mobile;
    private String address;
    private Integer credit;

    public UserLoginRetVo(String token, User user){
        this.token = token;
        this.credit = user.getCredit();
        this.mobile = user.getDecryptMobile();
        this.address = user.getDecryptAddress();
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
