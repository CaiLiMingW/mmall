package org.clm.Service;

import org.clm.common.ServiceResponse;

/**
 * @author Ccc
 * @date 2018/10/10 0010 下午 2:33
 */
public interface IProductService {
    ServiceResponse getProductDetail(Integer produtcId);

    ServiceResponse getProductList( Integer categoryId, Integer pageNum, Integer pageSize, String keyword, String orderBy);
}

