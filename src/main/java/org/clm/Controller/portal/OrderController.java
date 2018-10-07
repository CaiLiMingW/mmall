package org.clm.Controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import org.clm.Pojo.User;
import org.clm.Service.IOrderService;
import org.clm.Service.IUserService;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Service;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Ccc
 * @date 2018/10/5 0005 下午 10:54
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;







    @RequestMapping("/create.do")
    public ServiceResponse createOrder(HttpSession session,Integer shippingId){
        ServiceResponse<User> response = iUserService.checkUserLogin(session);
        if (response.isSuccess()){
            return iOrderService.createOrder(response.getData().getId(),shippingId);
        }
        return response;
    }

    @RequestMapping("/get_order_cart_product.do")
    public ServiceResponse getOrderCartProduct(HttpSession session){
        ServiceResponse<User> response = iUserService.checkUserLogin(session);
        if (response.isSuccess()){
            return iOrderService.getOrderProduct(response.getData().getId());
        }
        return response;
    }

    @RequestMapping("/list.do")
    public ServiceResponse getOrderList(HttpSession session,
                                        @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        ServiceResponse<User> response = iUserService.checkUserLogin(session);
        if (response.isSuccess()){
            return iOrderService.getOrderListByUserId(response.getData().getId(),pageNum,pageSize);
        }
        return response;
    }









    @RequestMapping("/pay.do")
    public ServiceResponse pay(HttpSession session , Long orderNo, HttpServletRequest request){
        ServiceResponse<User> response = iUserService.checkUserLogin(session);
        String path = request.getSession().getServletContext().getRealPath("upload");
        if (response.isSuccess()){
            return iOrderService.pay(orderNo,response.getData().getId(),path);
        }
        return response;
    }

    @RequestMapping("/alipay_callback.do")
    public Object alipaycallback(HttpServletRequest request){
        ServiceResponse Response = iOrderService.alipayCallback(request);
        if(Response.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping("/query_order_pay_status.do")
    public ServiceResponse queryOrderPayStatus(HttpSession session , Long orderNo){
        ServiceResponse<User> response = iUserService.checkUserLogin(session);
        if (response.isSuccess()){
            return iOrderService.getOrderPayStatus(orderNo);
        }
        return response;
    }
}
