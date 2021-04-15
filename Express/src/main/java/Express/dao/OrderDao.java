package Express.dao;

import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Express.mapper.OrderPoMapper;
import Express.model.bo.Order;
import Express.model.po.OrderPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class OrderDao {

    @Autowired
    private OrderPoMapper orderPoMapper;

    /**
     * 将订单插入数据库
     * @author snow create 2021/04/15 16:07
     * @param order
     * @return
     */
    public ReturnObject<Order> insertOrder(Order order){
        try {
            OrderPo orderPo = order.createPo();
            orderPo.setGmtCreate(LocalDateTime.now());
            int effectRows = orderPoMapper.insert(orderPo);
            if(effectRows == 1){
                order.setId(orderPo.getId());
                return new ReturnObject<>(order);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 更新订单
     * @author snow create 2021/04/15 19:48
     * @param order
     * @return
     */
    public ReturnObject alterOrder(Order order){
        try {
            OrderPo orderPo = order.createPo();
            orderPo.setGmtModified(LocalDateTime.now());
            int effectRows = orderPoMapper.updateByPrimaryKey(orderPo);
            if(effectRows == 1){
                return new ReturnObject(ResponseCode.OK);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 根据id查找订单
     * @author snow create 2021/04/15 19:50
     * @param orderId
     * @return
     */
    public ReturnObject<Order> findOrderById(Long orderId){
        try {
            OrderPo orderPo = orderPoMapper.selectByPrimaryKey(orderId);
            if(orderPo != null && orderPo.getDeleted() != (byte)1){
                return new ReturnObject<>(new Order(orderPo));
            }
            else {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOT_EXIST);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }
}
