package org.clm.Service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.clm.Dao.ProductMapper;
import org.clm.Pojo.Product;
import org.clm.VO.ProductListVo;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ccc
 * @date 2018/10/10 0010 下午 2:34
 */
@Service
public class IProductServiceImpl implements IProductService{
    @Autowired
    private ProductMapper productMapper;


    @Override
    public ServiceResponse getProductDetail(Integer productId) {
        if (productId==null){
            return ServiceResponse.createByErrorMessage("产品ID错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServiceResponse.createByErrorMessage("该商品已下架或删除");
        }
        return ServiceResponse.createBySucces(product);
    }

    @Override
    public ServiceResponse getProductList( Integer categoryId, Integer pageNum, Integer pageSize, String keyword, String orderBy) {
       /* if (categoryId==null){
            return ServiceResponse.createByErrorMessage("categoryId错误");
        }*/
        PageHelper.startPage(pageNum,pageSize);
        List<ProductListVo> productListVos = productMapper.selectProductBycategoryIdAndKeywordOrdeBy(categoryId, keyword,
                orderBy.equals("") ? Const.OrderBy.NULL : orderBy.equals("price_desc") ? Const.OrderBy.PRICE_DESC : Const.OrderBy.PRICE_ASC);
        if (productListVos==null){
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        PageInfo pageInfo = new PageInfo(productListVos);

        return ServiceResponse.createBySucces(pageInfo);
    }
}
