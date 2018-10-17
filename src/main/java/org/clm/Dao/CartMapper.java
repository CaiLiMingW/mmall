package org.clm.Dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.clm.Pojo.Cart;
import org.clm.VO.CartProductVo;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<CartProductVo> selectAll(Integer userId);

    Cart checkStockAndCar(@Param("userId") Integer userId,@Param("productId") Integer productId);

    int deleteByIds(@Param("userId") Integer userId,@Param("productIdList")List<String> productIdList);

    List<Cart> selectByIdAll(Integer userId);

    int updateCheck(@Param("userId") Integer userId, @Param("checked") Integer checked, @Param("productId") Integer productId);

    int selectCartProductCount(Integer userId);

    List<Cart> selectCartCheckedByUserId(Integer userId);




    /*Cart selectCarByIdAndProtuct(Integer userId, Integer productId);*/
}