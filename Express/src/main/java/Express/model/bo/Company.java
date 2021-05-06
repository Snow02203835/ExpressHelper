package Express.model.bo;

import lombok.Data;
import Core.model.VoObject;
import java.io.Serializable;
import Express.model.po.CompanyPo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author snow create 2021/05/06 20:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company implements VoObject, Serializable {

    private Integer id;
    private String name;

    public Company(CompanyPo companyPo){
        this.id = companyPo.getId();
        this.name = companyPo.getName();
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
