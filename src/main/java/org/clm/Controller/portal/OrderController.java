package org.clm.Controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import org.clm.Pojo.Order;
import org.clm.Pojo.User;
import org.clm.Service.IOrderService;
import org.clm.Service.IUserService;
import org.clm.VO.OrderVo;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;

import org.clm.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Service;
import java.sql.Connection;
import java.util.HashMap;
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
    @Autowired
    private RabbitTemplate rabbitTemplate;








    @RequestMapping("/create.do")
    public ServiceResponse createOrder(HttpServletRequest request,Integer shippingId){

        User user = (User)request.getAttribute("user");

        Map map = new HashMap();
        map.put("userId",user.getId());
        map.put("shippingId",shippingId);

//        Order ordermessage = new Order();
//        ordermessage.setUserId(user.getId());
//        ordermessage.setShippingId(shippingId);
        //将订单请求发送到消息队列
        rabbitTemplate.convertAndSend(Const.Routingkey.ORDERMESSAGE, JsonUtil.objToString(map));
        //return iOrderService.createOrder(22,shippingId);
        return ServiceResponse.createBySuccess("排队中",new OrderVo());
    }

    @RequestMapping("/get_order_cart_product.do")
    public ServiceResponse getOrderCartProduct(HttpServletRequest request){
        User user = (User)request.getAttribute("user");
        return iOrderService.getOrderProduct(user.getId());
    }

    @RequestMapping("/list.do")
    public ServiceResponse getOrderList(HttpServletRequest request,
                                        @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User user = (User)request.getAttribute("user");
        return iOrderService.getOrderListByUserId(user.getId(),pageNum,pageSize);
    }


    @RequestMapping("/detail.do")
    public ServiceResponse getOrderDeatil(HttpServletRequest request,Long orderNo){
        User user = (User)request.getAttribute("user");
        return iOrderService.getOrderDeatilByOrderNo(user.getId(),orderNo);
    }

    @RequestMapping("/cancel.do")
    public ServiceResponse cancelOrder(HttpServletRequest request,Long orderNo){
        User user = (User)request.getAttribute("user");
        return iOrderService.cancelOrderByOrderNo(user.getId(),orderNo);
    }










    @RequestMapping("/pay.do")
    public ServiceResponse pay(Long orderNo, HttpServletRequest request){
        User user = (User)request.getAttribute("user");
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),path);
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
    public ServiceResponse queryOrderPayStatus(HttpServletRequest request , Long orderNo){
        User user = (User)request.getAttribute("user");
        return iOrderService.getOrderPayStatus(orderNo);
    }
}
