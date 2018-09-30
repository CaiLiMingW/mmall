package org.clm.Dao;

import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.clm.Pojo.Product;
import org.clm.common.ServiceResponse;

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
}