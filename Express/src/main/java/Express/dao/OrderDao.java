package Express.dao;

import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Express.mapper.OrderPoMapper;
import Express.model.bo.Order;
import Express.model.po.OrderPo;
import Express.model.po.OrderPoExample;
import Express.util.OrderStatus;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
            order.setGmtCreate(LocalDateTime.now());
            OrderPo orderPo = order.createPo();
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
     *            modified 2021/05/26 15:40
     * @param orderId 订单id
     * @param selectDeleted 逻辑删除是否可见
     * @return 订单详细
     */
    public ReturnObject<Order> findOrderById(Long orderId, boolean selectDeleted){
        try {
            OrderPo orderPo = orderPoMapper.selectByPrimaryKey(orderId);
            if(orderPo != null && (selectDeleted || orderPo.getDeleted() != (byte)1)){
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

    /**
     * 根据需求id返回最后一个相关订单信息
     * @author snow create 2021/05/06 16:20
     * @param demandId 需求id
     * @return 操作结果
     */
    public Order findLastOrderByDemandId(Long demandId){
        try {
            OrderPoExample example = new OrderPoExample();
            OrderPoExample.Criteria criteria = example.createCriteria();
            criteria.andDemandIdEqualTo(demandId);
            List<OrderPo> orderPos = orderPoMapper.selectByExample(example);
            if(orderPos != null && orderPos.size() != 0){
                return new Order(orderPos.get(orderPos.size()-1));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据需求id查找相关订单
     * @author snow create 2021/04/16 00:23
     * @param demandId
     * @return
     */
    public List<Order> findOrderByDemandId(Long demandId){
        try {
            OrderPoExample example = new OrderPoExample();
            OrderPoExample.Criteria criteria = example.createCriteria();
            criteria.andDemandIdEqualTo(demandId);
            List<OrderPo> orderPos = orderPoMapper.selectByExample(example);
            if(orderPos != null && orderPos.size() != 0){
                List<Order> orders = new ArrayList<>(orderPos.size());
                for(OrderPo orderPo : orderPos){
                    orders.add(new Order(orderPo));
                }
                return orders;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据条件查找订单
     * @author snow create 2021/04/17 00:34
     *            modified 2021/05/20 01:05
     *            modified 2021/05/26 15:15
     * @param receiverId 接单者id
     * @param status 状态
     * @param deleted 逻辑删除是否可见
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    public PageInfo<OrderPo> findOrdersWithCondition(Long receiverId , Byte status, Byte deleted,
                                                     LocalDateTime startTime, LocalDateTime endTime){
        try {
            OrderPoExample example = new OrderPoExample();
            OrderPoExample.Criteria criteria = example.createCriteria();
            if(receiverId != null){
                criteria.andReceiverIdEqualTo(receiverId);
            }
            if(status != null){
                criteria.andStatusEqualTo(status);
            }
            if(deleted != null){
                criteria.andDeletedEqualTo(deleted);
            }
            if(startTime != null){
                criteria.andGmtModifiedGreaterThanOrEqualTo(startTime);
            }
            if(endTime != null){
                criteria.andGmtModifiedLessThanOrEqualTo(endTime);
            }
            return new PageInfo<>(orderPoMapper.selectByExample(example));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
