package Express.dao;

import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Express.mapper.ImagePoMapper;
import Express.model.bo.Image;
import Express.model.po.ImagePo;
import Express.model.po.ImagePoExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author snow create 2021/04/17 14:19
 */
@Repository
public class ImageDao {
    @Autowired
    private ImagePoMapper imagePoMapper;

    /**
     * 根据文件MD5判断文件是否已存在
     * @author snow create 2021/04/17 14:25
     * @param md5 图片文件MD5
     * @return 文件路径
     */
    public ReturnObject<String> isImageHasExist(String md5){
        try {
            ImagePoExample example = new ImagePoExample();
            ImagePoExample.Criteria criteria = example.createCriteria();
            criteria.andMd5EqualTo(md5);
            List<ImagePo> imagePos = imagePoMapper.selectByExample(example);
            if(imagePos == null || imagePos.size() == 0){
                return new ReturnObject<>(ResponseCode.IMG_NOT_EXIST);
            }
            else{
                return new ReturnObject<>(imagePos.get(0).getName());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 插入图片信息至数据库
     * @author snow create 2021/04/17 14:59
     * @param image 图片信息
     * @return 插入结果
     */
    public ReturnObject<String> insertImage(Image image){
        try {
            ImagePo imagePo = image.createPo();
            imagePo.setGmtCreate(LocalDateTime.now());
            int effectRows = imagePoMapper.insert(imagePo);
            if (effectRows == 1){
                return new ReturnObject(image.getName());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }
}
