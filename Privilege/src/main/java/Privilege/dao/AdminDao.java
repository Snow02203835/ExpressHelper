package Privilege.dao;

import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Privilege.mapper.AdminPoMapper;
import Privilege.model.bo.Admin;
import Privilege.model.po.AdminPo;
import Privilege.model.po.AdminPoExample;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class AdminDao {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private AdminPoMapper adminPoMapper;

    /**
     * 根据id查找用户
     * @author snow create 2021/03/27 20:12
     * @param userId
     * @return
     */
    public ReturnObject<Admin> findAdminById(Long userId){
        if(userId != null){
            try {
                AdminPo adminPo = adminPoMapper.selectByPrimaryKey(userId);
                if(adminPo != null){
                    return new ReturnObject<>(new Admin(adminPo));
                }
                else{
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOT_EXIST);
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
            }
        }
        return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
    }

    /**
     * 根据用户名查找用户
     * @author snow create 2021/03/27 20:14
     * @param userName
     * @return
     */
    public ReturnObject<Admin> findAdminByUserName(String userName){
        try {
            AdminPoExample example = new AdminPoExample();
            AdminPoExample.Criteria criteria = example.createCriteria();
            criteria.andUserNameEqualTo(userName);
            List<AdminPo> adminPos = adminPoMapper.selectByExample(example);
            if(adminPos != null && adminPos.size() > 0){
                return new ReturnObject<>(new Admin(adminPos.get(0)));
            }
            else{
                return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 根据条件查找用户
     * @author snow create 2021/04/07 08:33
     * @param role
     * @param userName
     * @return
     */
    public PageInfo<AdminPo> findAdminsInCondition(Byte role, String userName){
        try{
            AdminPoExample example = new AdminPoExample();
            AdminPoExample.Criteria criteria = example.createCriteria();
            if(role != null){
                criteria.andRoleEqualTo(role);
            }
            if(userName != null){
                criteria.andUserNameLike(userName + "%");
            }
            List<AdminPo> adminPos = adminPoMapper.selectByExample(example);
            return new PageInfo<>(adminPos);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 插入用户信息
     * @author snow create 2021/04/15 13:39
     * @param admin
     * @return
     */
    public ReturnObject<Admin> insertAdmin(Admin admin){
        try {
            if(admin.getUserName() != null && isUserNameAlreadyExist(admin.getUserName())){
                return new ReturnObject<>(ResponseCode.USER_NAME_REGISTERED);
            }
            if(admin.getEmail() != null && isEmailAlreadyExist(admin.getEmail())){
                return new ReturnObject<>(ResponseCode.EMAIL_REGISTERED);
            }
            if(admin.getMobile() != null && isMobileAlreadyExist(admin.getMobile())){
                return new ReturnObject<>(ResponseCode.MOBILE_REGISTERED);
            }
            AdminPo adminPo = admin.createAdminPo();
            adminPo.setGmtCreate(LocalDateTime.now());
            int effectRows = adminPoMapper.insertSelective(adminPo);
            if(effectRows == 1){
                admin.setId(adminPo.getId());
                return new ReturnObject<>(admin);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 判断用户名是否已存在
     * @author snow create 2021/03/27 20:17
     * @param userName
     * @return
     */
    public Boolean isUserNameAlreadyExist(String userName){
        AdminPoExample example = new AdminPoExample();
        AdminPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<AdminPo> adminPos = adminPoMapper.selectByExample(example);
        return adminPos != null && adminPos.size() != 0;
    }

    /**
     * 判断邮箱是否已存在
     * @author snow create 2021/03/27 20:18
     * @param email
     * @return
     */
    public Boolean isEmailAlreadyExist(String email){
        AdminPoExample example = new AdminPoExample();
        AdminPoExample.Criteria criteria = example.createCriteria();
        criteria.andEmailEqualTo(email);
        List<AdminPo> adminPos = adminPoMapper.selectByExample(example);
        return adminPos != null && adminPos.size() != 0;
    }

    /**
     * 判断电话号码是否已存在
     * @author snow create 2021/03/27 20:19
     * @param mobile
     * @return
     */
    public Boolean isMobileAlreadyExist(String mobile){
        AdminPoExample example = new AdminPoExample();
        AdminPoExample.Criteria criteria = example.createCriteria();
        criteria.andMobileEqualTo(mobile);
        List<AdminPo> adminPos = adminPoMapper.selectByExample(example);
        return adminPos != null && adminPos.size() != 0;
    }

    /**
     * 更新用户信息
     * @author snow create 2021/03/27 20:22
     * @param admin
     * @return
     */
    public ReturnObject updateAdminInformation(Admin admin){
        try {
            AdminPo adminPo = admin.createAdminPo();
            adminPo.setGmtModified(LocalDateTime.now());
            int effectRows = adminPoMapper.updateByPrimaryKeySelective(adminPo);
            if(effectRows == 1){
                return new ReturnObject(ResponseCode.OK);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 判断是否重复请求验证码
     * @author snow create 2021/01/17 23:06
     * @param ipAddress
     * @return
     */
    public Boolean isAllowRequestForVerifyCode(String ipAddress){
        String key = "ip_" + ipAddress;
        if(redisTemplate.hasKey(key)){
            return false;
        }
        redisTemplate.opsForValue().set(key, ipAddress);
        redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        return true;
    }

    /**
     * 将验证码放入Redis
     * @author snow create 2021/01/17 23:17
     * @param verifyCode
     * @param studentId
     */
    public void putVerifyCodeIntoRedis(String verifyCode, String studentId){
        String key = "cp_" + verifyCode;
        redisTemplate.opsForValue().set(key, studentId);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
    }

    /**
     * 从验证码中取出id
     * @author snow create 2021/01/17 23:58
     * @param verifyCode
     * @return
     */
    public Long getAdminIdByVerifyCode(String verifyCode){
        String key = "cp_" + verifyCode;
        if(!redisTemplate.hasKey(key)){
            return null;
        }
        return Long.valueOf(redisTemplate.opsForValue().get(key).toString());
    }

    /**
     * 修改密码成功之后让验证码失效
     * @author snow create 2021/01/18 10:32
     * @param verifyCode
     */
    public void disableVerifyCodeAfterSuccessfullyModifyPassword(String verifyCode){
        String key = "cp_" + verifyCode;
        if(redisTemplate.hasKey(key)){
            redisTemplate.expire(key, 1, TimeUnit.MILLISECONDS);
        }
    }
}
