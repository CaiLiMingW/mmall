package org.clm.Dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.clm.Pojo.Order;
import org.clm.Pojo.OrderItem;

import java.util.Date;
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

    List<OrderItem> selectByUserId(@Param("userId") Integer userId);

    List<OrderItem> selectByOrderNoKey(Long orderNo);

    List<OrderItem> selectOrderByStatusCreatetime(@Param("status") int status, @Param("date") String date);

    @Select("select id,order_no from mmall_order_item where ")
    List<OrderItem> delOrderNoAndCreatetime();

    @Delete("delete from mmall_order_item where order_no = #{orderNo}  ")
    int deletebyOrderNo(@Param("orderNo") Long orderNo);
}