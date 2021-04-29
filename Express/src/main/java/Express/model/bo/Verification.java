package Express.model.bo;

import Core.model.VoObject;
import Express.model.po.VerificationPo;
import Express.model.vo.VerificationVo;
import Express.util.VerificationStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author snow create 2021/04/29 00:58
 *            modified 2021/04/29 14:08
 */
@Data
public class Verification implements VoObject, Serializable {
    private Long id;
    private Long userId;
    private Byte status;
    private String coverImg;
    private String contentImg;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Verification(Long userId, VerificationVo vo){
        this.status = VerificationStatus.UNHANDLED.getCode();
        this.userId = userId;
        this.coverImg = vo.getCoverImg();
        this.contentImg = vo.getContentImg();
    }

    public Verification(VerificationPo po){
        this.id = po.getId();
        this.userId = po.getUserId();
        this.status = po.getStatus();
        this.coverImg = po.getCoverImg();
        this.contentImg = po.getContentImg();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    public VerificationPo createPo(){
        VerificationPo po = new VerificationPo();
        po.setId(this.id);
        po.setUserId(this.userId);
        po.setStatus(this.status);
        po.setCoverImg(this.coverImg);
        po.setContentImg(this.contentImg);
        po.setGmtCreate(this.gmtCreate);
        po.setGmtModified(this.gmtModified);
        return po;
    }

    public void updateInfoWithVo(VerificationVo verificationVo){
        if(!verificationVo.getCoverImg().isBlank()){
            this.coverImg = verificationVo.getCoverImg();
        }
        if(!verificationVo.getContentImg().isBlank()){
            this.contentImg = verificationVo.getContentImg();
        }
        this.status = VerificationStatus.UNHANDLED.getCode();
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
