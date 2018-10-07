package org.clm.Service;

import org.clm.common.ServiceResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Ccc
 * @date 2018/10/5 0005 下午 10:55
 */
public interface IOrderService {

    ServiceResponse pay(Long orderNo, Integer userId, String path);

    ServiceResponse alipayCallback(HttpServletRequest request);

    ServiceResponse getOrderPayStatus(Long orderNo);

    ServiceResponse createOrder(Integer userId, Integer shippingId);

    ServiceResponse getOrderProduct(Integer userId);

    ServiceResponse getOrderListByUserId(Integer userId,Integer pageNum,Integer pageSize);
}
