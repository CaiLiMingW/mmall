package org.clm.Dao;

import org.apache.ibatis.annotations.Param;
import org.clm.Pojo.Order;
import org.clm.Pojo.OrderItem;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    void batchInsert(@Param("orderItemList")List<OrderItem> orderItemList);

    List<OrderItem> selectByUserId(Integer userId);
}