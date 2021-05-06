package Express.model.bo;

import lombok.Data;
import Core.model.VoObject;
import java.io.Serializable;
import Express.model.po.CampusPo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author snow create 2021/05/06 20:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campus implements VoObject, Serializable {

    private Integer id;
    private String name;
    private String building;

    public Campus(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    public Campus(CampusPo campusPo){
        this.id = campusPo.getId();
        this.name = campusPo.getName();
        this.building = campusPo.getBuilding();
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
