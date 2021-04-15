package Express.dao;

import Core.util.ResponseCode;
import Express.mapper.DemandPoMapper;
import Core.util.ReturnObject;
import Express.model.bo.Demand;
import Express.model.po.DemandPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class DemandDao {

    @Autowired
    private DemandPoMapper demandPoMapper;

    /**
     * 将需求插入数据库
     * @author snow create 2021/04/15 15:04
     * @param demand
     * @return
     */
    public ReturnObject<Demand> newDemand(Demand demand){
        try {
            DemandPo demandPo = demand.createPo();
            demandPo.setGmtCreate(LocalDateTime.now());
            int effectRows = demandPoMapper.insert(demandPo);
            if(effectRows == 1){
                demand.setId(demandPo.getId());
                return new ReturnObject<>(demand);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 更新需求
     * @author snow create 2021/04/15 15:29
     * @param demand
     * @return
     */
    public ReturnObject alterDemand(Demand demand){
        try {
            DemandPo demandPo = demand.createPo();
            demandPo.setGmtModified(LocalDateTime.now());
            int effectRows = demandPoMapper.updateByPrimaryKey(demandPo);
            if(effectRows == 1){
                return new ReturnObject(ResponseCode.OK);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 根据id查找需求
     * @author snow create 2021/04/15 15:53
     * @param demandId
     * @return
     */
    public ReturnObject<Demand> findDemandById(Long demandId){
        try {
            DemandPo demandPo = demandPoMapper.selectByPrimaryKey(demandId);
            if(demandPo != null && demandPo.getDeleted() != (byte)1){
                return new ReturnObject<>(new Demand(demandPo));
            }
            else {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOT_EXIST);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }
}
