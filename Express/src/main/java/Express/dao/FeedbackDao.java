package Express.dao;

import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Express.mapper.FeedBackPoMapper;
import Express.model.bo.Feedback;
import Express.model.po.FeedBackPo;
import Express.model.po.FeedBackPoExample;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
     * 更新用户反馈
     * @author snow create 2021/04/19 10:19
     * @param feedback 反馈信息
     * @return 操作结果
     */
    public ReturnObject updateFeedback(Feedback feedback){
        try {
            FeedBackPo feedBackPo = feedback.createPo();
            feedBackPo.setGmtModified(LocalDateTime.now());
            int effectRows = feedBackPoMapper.updateByPrimaryKey(feedBackPo);
            if(effectRows == 1){
                return new ReturnObject(ResponseCode.OK);
            }
            else{
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOT_EXIST);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 根据id查找反馈信息
     * @author snow create 2021/04/19 01:25
     *            modified 2021/04/19 10:30
     * @param feedBackId 反馈信息id
     * @return 反馈信息详细
     */
    public ReturnObject<Feedback> findFeedbackById(Long feedBackId){
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

    /**
     * 根据订单id查找用户反馈
     * @author snow create 2021/04/19 10:25
     * @param orderId 订单id
     * @return 反馈信息
     */
    public ReturnObject<Feedback> findFeedbackByOrderId(Long orderId){
        try {
            FeedBackPoExample example = new FeedBackPoExample();
            FeedBackPoExample.Criteria criteria = example.createCriteria();
            criteria.andOrderIdEqualTo(orderId);
            List<FeedBackPo> feedBackPos = feedBackPoMapper.selectByExample(example);
            if(feedBackPos != null && feedBackPos.size() != 0){
                return new ReturnObject<>(new Feedback(feedBackPos.get(0)));
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
     * 根据条件查找用户反馈
     * @author snow create 2021/04/19 10:33
     * @param userId 用户id
     * @param status 反馈状态
     * @param deleted 逻辑删除是否可见
     * @param content 反馈内容
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 反馈信息列表
     */
    public PageInfo<FeedBackPo> findFeedbackWithCondition(Long userId, Byte status,
                                                          Byte deleted, String content,
                                                          LocalDateTime startTime, LocalDateTime endTime){
        try {
            FeedBackPoExample example = new FeedBackPoExample();
            FeedBackPoExample.Criteria criteria = example.createCriteria();
            if(userId != null){
                criteria.andUserIdEqualTo(userId);
            }
            if(status != null){
                criteria.andStatusEqualTo(status);
            }
            if(deleted != null){
                criteria.andDeletedEqualTo(deleted);
            }
            if(content != null){
                criteria.andContentLike(content);
            }
            if(startTime != null){
                criteria.andGmtModifiedGreaterThanOrEqualTo(startTime);
            }
            if(endTime != null){
                criteria.andGmtModifiedLessThanOrEqualTo(endTime);
            }
            return new PageInfo<>(feedBackPoMapper.selectByExample(example));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
