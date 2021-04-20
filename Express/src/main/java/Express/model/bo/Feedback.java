package Express.model.bo;

import Core.model.VoObject;
import Express.model.po.FeedBackPo;
import Express.model.vo.FeedbackVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author snow create 2021/04/19 01:09
 *            modified 2021/04/19 11:19
 *            modified 2021/04/20 19:30
 */
@Data
public class Feedback implements VoObject, Serializable {
    private Long id;
    private Long userId;
    private Long orderId;
    private Byte type;
    private Byte status;
    private Byte deleted;
    private String img;
    private String content;
    private String response;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Feedback(Long userId, FeedbackVo feedbackVo) {
        this.userId = userId;
        this.status = (byte)0;
        this.deleted = (byte)0;
        this.img = feedbackVo.getImg();
        this.type = feedbackVo.getType();
        this.orderId = feedbackVo.getOrderId();
        this.content = feedbackVo.getContent();
    }

    public Feedback(FeedBackPo feedBackPo){
        this.id = feedBackPo.getId();
        this.userId = feedBackPo.getUserId();
        this.orderId = feedBackPo.getOrderId();
        this.type = feedBackPo.getType();
        this.status = feedBackPo.getStatus();
        this.deleted = feedBackPo.getDeleted();
        this.img = feedBackPo.getImg();
        this.content = feedBackPo.getContent();
        this.response = feedBackPo.getResponse();
        this.gmtCreate = feedBackPo.getGmtCreate();
        this.gmtModified = feedBackPo.getGmtModified();
    }

    public FeedBackPo createPo(){
        FeedBackPo feedBackPo = new FeedBackPo();
        feedBackPo.setId(this.id);
        feedBackPo.setUserId(this.userId);
        feedBackPo.setOrderId(this.orderId);
        feedBackPo.setType(this.type);
        feedBackPo.setStatus(this.status);
        feedBackPo.setDeleted(this.deleted);
        feedBackPo.setImg(this.img);
        feedBackPo.setContent(this.content);
        feedBackPo.setResponse(this.response);
        feedBackPo.setGmtCreate(this.gmtCreate);
        feedBackPo.setGmtModified(this.gmtModified);
        return feedBackPo;
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
