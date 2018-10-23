package org.clm.Dao;

import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.clm.Pojo.Product;
import org.clm.VO.ProductListVo;
import org.clm.common.ServiceResponse;

import javax.xml.ws.Service;
import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer productId);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectAllProduct();

    List<Product> selectByIdAndName(@Param("productName") String productName,@Param("productId")Integer productId);

    List<ProductListVo> selectProductBycategoryIdAndKeywordOrdeBy(@Param("categoryId") Integer categoryId,
                                                                  @Param("keyword") String keyword,
                                                                  @Param("orderBy") String orderBy);
    @Update("update mmall_product set stock = #{stock}-#{quantity} where id = #{productId} and stock = #{stock}")
    int reductStock(@Param("productId") Integer productId,@Param("stock") Integer stock,@Param("quantity") Integer quantity);

    @Update("update mmall_product set stock = #{stock}+#{quantity} where id = #{productId} and stock = #{stock}")
    int addStock(@Param("productId") Integer productId,@Param("stock") Integer stock,@Param("quantity") Integer quantity);
}