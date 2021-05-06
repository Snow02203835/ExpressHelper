package Express.dao;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import Express.model.po.*;
import Express.model.bo.Campus;
import Express.model.bo.Company;
import Express.model.bo.Address;
import Express.model.vo.CampusRetVo;
import Express.model.vo.CompanyRetVo;
import Express.model.vo.AddressRetVo;
import Express.mapper.AddressPoMapper;
import Express.mapper.CompanyPoMapper;
import Express.mapper.CampusPoMapper;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Repository
public class StaticDao {
    private final String campusKeys = "campusData";
    private final String companyKeys = "companyData";
    private final String addressKeys = "addressData";
    @Autowired
    private CampusPoMapper campusMapper;
    @Autowired
    private CompanyPoMapper companyMapper;
    @Autowired
    private AddressPoMapper addressMapper;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 把校园数据放进redis中
     * @author snow create 2021/05/07 00:15
     * @return 操作结果
     */
    private boolean putCampusDataIntoRedis(){
        try {
            CampusPoExample example = new CampusPoExample();
            List<CampusPo> campusPos = campusMapper.selectByExample(example);
            BoundSetOperations campusKey = redisTemplate.boundSetOps(campusKeys);
            for(CampusPo po : campusPos){
                campusKey.add(new Campus(po));
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 把快递公司数据放进redis中
     * @author snow create 2021/05/07 00:18
     * @return 操作结果
     */
    private boolean putCompanyDataIntoRedis(){
        try {
            CompanyPoExample example = new CompanyPoExample();
            List<CompanyPo> companyPos = companyMapper.selectByExample(example);
            BoundSetOperations companyKey = redisTemplate.boundSetOps(companyKeys);
            for(CompanyPo po : companyPos){
                companyKey.add(new Company(po));
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 把地址数据放进redis中
     * @author snow create 2021/05/06 20:40
     * @return 操作结果
     */
    private boolean putAddressDataIntoRedis(){
        try {
            AddressPoExample example = new AddressPoExample();
            List<AddressPo> addressPos = addressMapper.selectByExample(example);
            BoundSetOperations addressKey = redisTemplate.boundSetOps(addressKeys);
            for(AddressPo po : addressPos){
                addressKey.add(new Address(po));
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从redis中获取校园数据，若没有则从数据库中取数据放入Redis
     * @author snow create 2021/05/07 00:21
     * @return 数据/null
     */
    public CampusRetVo getCampus(){
        try{
            boolean result = true;
            Set<Serializable> campusSet = redisTemplate.boundSetOps(campusKeys).members();
            while ((campusSet == null || campusSet.size() == 0) && result){
                result = putCampusDataIntoRedis();
                campusSet = redisTemplate.boundSetOps(campusKeys).members();
            }
            List<Campus> campusList = new ArrayList<>(campusSet.size());
            for (Serializable object : campusSet){
                campusList.add((Campus) object);
            }
            return new CampusRetVo(campusList);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从redis中获取快递公司数据，若没有则从数据库中取数据放入Redis
     * @author snow create 2021/05/07 00:24
     * @return 数据/null
     */
    public CompanyRetVo getCompanies(){
        try{
            boolean result = true;
            Set<Serializable> companySet = redisTemplate.boundSetOps(companyKeys).members();
            while ((companySet == null || companySet.size() == 0) && result){
                result = putCompanyDataIntoRedis();
                companySet = redisTemplate.boundSetOps(companyKeys).members();
            }
            List<Company> companyList = new ArrayList<>(companySet.size());
            for (Serializable object : companySet){
                companyList.add((Company) object);
            }
            return new CompanyRetVo(companyList);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从redis中获取地址数据，若没有则从数据库中取数据放入Redis
     * @author snow create 2021/05/06 20:50
     * @return 数据/null
     */
    public AddressRetVo getAddresses(){
        try{
            boolean result = true;
            Set<Serializable> addressSet = redisTemplate.boundSetOps(addressKeys).members();
            while ((addressSet == null || addressSet.size() == 0) && result){
                result = putAddressDataIntoRedis();
                addressSet = redisTemplate.boundSetOps(addressKeys).members();
            }
            List<Address> addressList = new ArrayList<>(addressSet.size());
            for (Serializable object : addressSet){
                addressList.add((Address)object);
            }
            return new AddressRetVo(addressList);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
