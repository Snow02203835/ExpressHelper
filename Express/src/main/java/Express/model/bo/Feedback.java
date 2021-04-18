package Express.model.bo;

import Core.model.VoObject;
import Express.model.po.FeedBackPo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author snow create 2021/04/19 01:09
 */
@Data
public class Feedback implements VoObject, Serializable {
    private Long id;
    private Long userId;
    private Long orderId;
    private Byte status;
    private String content;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Feedback(Long userId, Long orderId, String content) {
        this.status = (byte)0;
        this.userId = userId;
        this.orderId = orderId;
        this.content = content;
    }

    public Feedback(FeedBackPo feedBackPo){
        this.id = feedBackPo.getId();
        this.userId = feedBackPo.getUserId();
        this.orderId = feedBackPo.getOrderId();
        this.status = feedBackPo.getStatus();
        this.content = feedBackPo.getContent();
        this.gmtCreate = feedBackPo.getGmtCreate();
        this.gmtModified = feedBackPo.getGmtModified();
    }

    public FeedBackPo createPo(){
        FeedBackPo feedBackPo = new FeedBackPo();
        feedBackPo.setId(this.id);
        feedBackPo.setUserId(this.userId);
        feedBackPo.setOrderId(this.orderId);
        feedBackPo.setStatus(this.status);
        feedBackPo.setContent(this.content);
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
