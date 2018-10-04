package org.clm.Service;

import org.clm.Pojo.User;
import org.clm.VO.CartProductVo;
import org.clm.common.ServiceResponse;

import java.util.List;

/**
 * @author Ccc
 * @date 2018/10/4 0004 下午 3:10
 */
public interface ICartService {

    ServiceResponse getCartProductList(User user);

    ServiceResponse addCartProduct(User user,Integer productId, Integer count);

    ServiceResponse updateCart(User data, Integer productId,Integer count);

    ServiceResponse deleteCartProduct(User user,String ids);


    ServiceResponse updateCheck(User user, Integer checked,Integer productId);
}
