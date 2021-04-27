package Express.dao;

import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Express.mapper.UserPoMapper;
import Express.model.bo.User;
import Express.model.po.UserPo;
import Express.model.po.UserPoExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author snow create 2021/04/27 19:49
 */
@Repository
public class UserDao {
    @Autowired
    private UserPoMapper userMapper;

    /**
     * 根据用户id查找用户
     * @author snow create 2021/04/27 20:26
     * @param userId 用户id
     * @return 用户Bo
     */
    public ReturnObject<User> findUserById(Long userId){
        try {
            UserPo userPo = userMapper.selectByPrimaryKey(userId);
            if(userPo != null){
                return new ReturnObject<>(new User(userPo));
            }
            else{
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOT_EXIST);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 根据用户id查找用户
     * @author snow create 2021/04/27 20:28
     * @param openId 用户openId
     * @return 用户Bo
     */
    public ReturnObject<User> findUserByOpenId(String openId){
        try {
            UserPoExample example = new UserPoExample();
            UserPoExample.Criteria criteria = example.createCriteria();
            criteria.andOpenIdEqualTo(openId);
            List<UserPo> userPos = userMapper.selectByExample(example);
            if(userPos != null && userPos.size() != 0){
                return new ReturnObject<>(new User(userPos.get(0)));
            }
            else{
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOT_EXIST);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 新增用户
     * @author snow create 2021/04/27 20:41
     * @param user 用户信息
     * @return 操作结果
     */
    public ReturnObject insertNewUser(User user){
        try {
            user.setGmtCreate(LocalDateTime.now());
            UserPo userPo = user.createPo();
            int effectRows = userMapper.insert(userPo);
            if(effectRows == 1){
                user.setId(userPo.getId());
                return new ReturnObject(ResponseCode.OK);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 用户更新信息
     * @author snow create 2021/04/27 21:33
     * @param user 用户信息bo
     * @return 操作结果
     */
    public ResponseCode updateUserInfo(User user){
        try {
            UserPo userPo = user.createPo();
            userPo.setGmtModified(LocalDateTime.now());
            int effectRows = userMapper.updateByPrimaryKey(userPo);
            if(effectRows == 1){
                return ResponseCode.OK;
            }
            else{
                return ResponseCode.RESOURCE_ID_NOT_EXIST;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseCode.INTERNAL_SERVER_ERR;
    }

}
