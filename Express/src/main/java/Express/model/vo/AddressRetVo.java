package Express.model.vo;

import Core.model.VoObject;
import Express.model.bo.Address;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author snow create 2021/05/06 20:26
 */
@Data
public class AddressRetVo implements VoObject, Serializable {
    private List<Address> addressList;

    public AddressRetVo(List<Address> addressList) {
        this.addressList = addressList;
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
