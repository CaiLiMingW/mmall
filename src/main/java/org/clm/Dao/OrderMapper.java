package org.clm.Dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.clm.Pojo.Order;
import org.clm.VO.OrderVo;

import java.util.Date;
import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderByIdAndUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);

    Order selectByOrderNo(Long orderNo);

    List<Order> selectByUserId(@Param("userId") Integer userId);

    List<Order> selectByOrderNoKey(Long orderNo);

    List<OrderVo> selectOrderVoByKey(@Param("orderNo") Long orderNo);

    int closeOrderByOrderNo(Long orderNo);

    @Delete("delete from mmall_order where order_no =  #{orderNo} ")
    int deletebyOrderNo(@Param("orderNo") Long orderNo);

    @Select("select order_no from mmall_order where update_time <= #{date} and status = #{status} ")
    List<Order> selectByStatusAndCreatetime(@Param("status") int status, @Param("date") Date date);
}