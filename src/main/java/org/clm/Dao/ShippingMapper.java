package org.clm.Dao;

import org.apache.ibatis.annotations.Param;
import org.clm.Pojo.Shipping;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int checkShippingById(Integer shippingId);

    Shipping selectByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    List<Shipping> selectByPrimaryKeyList(Integer userId);

    String selectReceiverNameByShippingId(Integer shippingId);
}