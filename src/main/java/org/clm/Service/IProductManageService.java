package org.clm.Service;

import com.github.pagehelper.PageInfo;
import org.clm.Pojo.Product;
import org.clm.VO.ProductDetailVo;
import org.clm.common.ServiceResponse;

/**
 * @author Ccc
 * @date 2018/9/29 0029 下午 6:51
 */
public interface IProductManageService {

    ServiceResponse saveOrUpdateProduct(Product product);

    ServiceResponse<PageInfo> selectAllProduct(Integer pageNum, Integer pageSize);


    ServiceResponse setSaleStatus(Integer productId, Integer status);

    ServiceResponse<ProductDetailVo> geDetail(Integer productId);

    ServiceResponse<PageInfo> searchProductByIdAndName(String productName,Integer productId,int pageNum,int pageSize);


}
