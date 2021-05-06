package Express.model.bo;

import lombok.Data;
import Core.model.VoObject;
import java.io.Serializable;
import Express.model.po.AddressPo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author snow create 2021/05/06 20:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address implements VoObject, Serializable {

    private Integer id;
    private Integer campusId;
    private String companyId;
    private String address;

    public Address(AddressPo addressPo){
        this.id = addressPo.getId();
        this.campusId = addressPo.getCampusId();
        this.companyId = addressPo.getCompanyId();
        System.out.println("Address: " + addressPo.getCompanyId());
        this.address = addressPo.getCollectAddress();
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
