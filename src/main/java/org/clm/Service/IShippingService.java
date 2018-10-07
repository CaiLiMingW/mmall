package org.clm.Service;

import org.clm.Pojo.Shipping;
import org.clm.Pojo.User;
import org.clm.common.ServiceResponse;

/**
 * @author Ccc
 * @date 2018/10/5 0005 上午 10:57
 */
public interface IShippingService {
    ServiceResponse addShipping(User user, Shipping shipping);

    ServiceResponse deleteShipping(User user, Integer shippingId);

    ServiceResponse updateShipping(User user, Shipping shipping);

    ServiceResponse selectShipping(User user, Integer shippingId);

    ServiceResponse getShippingList(User user, Integer pageNum,Integer pageSize);
}
