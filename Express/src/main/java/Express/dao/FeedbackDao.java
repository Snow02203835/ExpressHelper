package Express.dao;

import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Express.mapper.FeedBackPoMapper;
import Express.model.bo.Feedback;
import Express.model.po.FeedBackPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class FeedbackDao {
    @Autowired
    private FeedBackPoMapper feedBackPoMapper;

    /**
     * 插入用户反馈至数据库
     * @author snow create 2021/04/19 01:23
     * @param feedback 反馈信息
     * @return 插入结果
     */
    public ReturnObject<Feedback> insertFeedback(Feedback feedback){
        try {
            feedback.setGmtCreate(LocalDateTime.now());
            FeedBackPo feedBackPo = feedback.createPo();
            int effectRows = feedBackPoMapper.insert(feedBackPo);
            if(effectRows == 1){
                feedback.setId(feedBackPo.getId());
                return new ReturnObject<>(feedback);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 根据id查找反馈信息
     * @author snow create 2021/04/19 01:25
     * @param feedBackId 反馈信息id
     * @return 反馈信息详细
     */
    public ReturnObject<Feedback> findFeedBackById(Long feedBackId){
        try {
            FeedBackPo feedBackPo = feedBackPoMapper.selectByPrimaryKey(feedBackId);
            if(feedBackPo != null){
                return new ReturnObject<>(new Feedback(feedBackPo));
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
}
