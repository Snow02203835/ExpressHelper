package Express.dao;

import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Express.mapper.VerificationPoMapper;
import Express.model.bo.Verification;
import Express.model.po.VerificationPo;
import Express.model.po.VerificationPoExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author snow create 2021/04/29 01:10
 */
@Repository
public class VerificationDao {
    @Autowired
    private VerificationPoMapper mapper;

    /**
     * 插入新的认证记录
     * @author snow create 2021/04/29 01:15
     * @param verification 认证信息
     * @return 插入结果错误码
     */
    public ResponseCode insertNewVerification(Verification verification){
        try {
            verification.setGmtCreate(LocalDateTime.now());
            VerificationPo verificationPo = verification.createPo();
            int effectRows = mapper.insert(verificationPo);
            if(effectRows == 1){
                verification.setId(verificationPo.getId());
                return ResponseCode.OK;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseCode.INTERNAL_SERVER_ERR;
    }

    /**
     * 根据用户id查找认证记录
     * @author snow create 2021/04/29 01:20
     * @param userId 用户id
     * @return 包含认证记录列表的ReturnObject
     */
    public ReturnObject<List<Verification>> findVerificationByUserId(Long userId){
        try {
            VerificationPoExample example = new VerificationPoExample();
            VerificationPoExample.Criteria criteria = example.createCriteria();
            criteria.andUserIdEqualTo(userId);
            List<VerificationPo> verificationPos = mapper.selectByExample(example);
            if(verificationPos == null && verificationPos.size() == 0){
                List<Verification> verifications = new ArrayList<>(verificationPos.size());
                for(VerificationPo verificationPo : verificationPos){
                    verifications.add(new Verification(verificationPo));
                }
                return new ReturnObject<>(verifications);
            }
            else {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOT_EXIST);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }
}
