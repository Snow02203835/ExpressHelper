package Express.model.bo;

import Express.model.po.ImagePo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author snow create 2021/04/17 14:51
 */
@Data
public class Image {
    private Long id;
    private Long userId;
    private String md5;
    private String name;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Image(Long userId, String md5, String name) {
        this.userId = userId;
        this.md5 = md5;
        this.name = name;
    }

    public ImagePo createPo(){
        ImagePo imagePo = new ImagePo();
        imagePo.setId(this.id);
        imagePo.setUserId(this.userId);
        imagePo.setMd5(this.md5);
        imagePo.setName(this.name);
        imagePo.setGmtCreate(this.gmtCreate);
        imagePo.setGmtModified(this.gmtModified);
        return imagePo;
    }
}
