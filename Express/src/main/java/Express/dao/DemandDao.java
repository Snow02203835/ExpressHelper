package Express.dao;

import Core.util.ResponseCode;
import Express.mapper.DemandPoMapper;
import Core.util.ReturnObject;
import Express.model.bo.Demand;
import Express.model.po.DemandPo;
import Express.model.po.DemandPoExample;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
     *            modified 2021/04/16 00:52
     * @param demandId
     * @param selectDeleted
     * @return
     */
    public ReturnObject<Demand> findDemandById(Long demandId, boolean selectDeleted){
        try {
            DemandPo demandPo = demandPoMapper.selectByPrimaryKey(demandId);
            if(demandPo != null && (selectDeleted || demandPo.getDeleted() != (byte)1)){
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

    /**
     * 根据条件查找需求
     * @author snow create 2021/04/16 11:03
     *            modified 2021/04/17 00:49
     * @param sponsorId
     * @param type
     * @param status
     * @param deleted
     * @param minPrice
     * @param maxPrice
     * @param address
     * @param destination
     * @param startTime
     * @param endTime
     * @return
     */
    public PageInfo<DemandPo> findDemandsWithCondition(Long sponsorId, Byte type, Byte status, Byte deleted,
                                                       Integer minPrice, Integer maxPrice,
                                                       String address, String destination,
                                                       LocalDateTime startTime, LocalDateTime endTime){
        try {
            DemandPoExample example = new DemandPoExample();
            DemandPoExample.Criteria criteria = example.createCriteria();
            if(sponsorId != null){
                criteria.andSponsorIdEqualTo(sponsorId);
            }
            if(type != null){
                criteria.andTypeEqualTo(type);
            }
            if(status != null){
                criteria.andStatusEqualTo(status);
            }
            if(deleted != null){
                criteria.andDeletedEqualTo(deleted);
            }
            if(minPrice != null){
                criteria.andPriceGreaterThanOrEqualTo(minPrice);
            }
            if(maxPrice != null){
                criteria.andPriceLessThanOrEqualTo(maxPrice);
            }
            if(address != null){
                criteria.andAddressLike(address);
            }
            if(destination != null){
                criteria.andDestinationLike(destination);
            }
            if(startTime != null){
                criteria.andGmtModifiedGreaterThanOrEqualTo(startTime);
            }
            if(endTime != null){
                criteria.andGmtModifiedLessThanOrEqualTo(endTime);
            }
            List<DemandPo> demandPos = demandPoMapper.selectByExample(example);
            return new PageInfo<>(demandPos);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}