package Express.model.vo;

import java.util.List;
import lombok.Data;
import Core.model.VoObject;
import java.io.Serializable;
import Express.model.bo.Company;

@Data
public class CompanyRetVo implements VoObject, Serializable {
    private List<Company> companyList;

    public CompanyRetVo(List<Company> companyList) {
        this.companyList = companyList;
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
