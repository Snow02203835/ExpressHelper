package Express.model.vo;

import lombok.Data;
import java.util.List;
import Core.model.VoObject;
import java.io.Serializable;
import Express.model.bo.Campus;

/**
 * @author snow create 2021/05/06 20:28
 */
@Data
public class CampusRetVo implements VoObject, Serializable {
    private List<Campus> campusList;

    public CampusRetVo(List<Campus> campusList) {
        this.campusList = campusList;
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
