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
import org.clm.util.RedisTemplateUtil;
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
    @Autowired
    private RedisTemplateUtil redisTemplateUtil;








    @RequestMapping("/create.do")
    public ServiceResponse createOrder(HttpServletRequest request,Integer shippingId){

        User user = (User)request.getSession().getAttribute("user");
        Map map = new HashMap();
        map.put("userId",user.getId());
        map.put("shippingId",shippingId);
        map.put("key",System.currentTimeMillis());
        //todo 订单号问题
        //将订单请求发送到消息队列
        rabbitTemplate.convertAndSend(Const.Routingkey.ORDERMESSAGE, JsonUtil.objToString(map));

        OrderVo orderVo = redisTemplateUtil.get(Const.objType.ORDER, "" + user.getId() + shippingId + map.get("key"));
        if (orderVo==null){
            //todo 修改定时任务
            for (int i = 0; i <11 ; i++) {
                try {
                    Thread.sleep(300);
                    orderVo = redisTemplateUtil.get(Const.objType.ORDER, "" + user.getId() + shippingId + map.get("key"));
                    if(i==10){
                        return ServiceResponse.createByErrorMessage("订单出错,请稍后重试试");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (orderVo!=null){
                    return ServiceResponse.createBySuccess("提交成功",orderVo);
                }
            }

        }

        return ServiceResponse.createBySuccess("提交成功",orderVo);
        //return iOrderService.createOrder(user.getID(),shippingId);
    }

    @RequestMapping("/get_order_cart_product.do")
    public ServiceResponse getOrderCartProduct(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        return iOrderService.getOrderProduct(user.getId());
    }

    @RequestMapping("/list.do")
    public ServiceResponse getOrderList(HttpServletRequest request,
                                        @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User user = (User)request.getSession().getAttribute("user");
        return iOrderService.getOrderListByUserId(user.getId(),pageNum,pageSize);
    }


    @RequestMapping("/detail.do")
    public ServiceResponse getOrderDeatil(HttpServletRequest request,Long orderNo){
        User user = (User)request.getSession().getAttribute("user");
        return iOrderService.getOrderDeatilByOrderNo(user.getId(),orderNo);
    }

    @RequestMapping("/cancel.do")
    public ServiceResponse cancelOrder(HttpServletRequest request,Long orderNo){
        User user = (User)request.getSession().getAttribute("user");
        return iOrderService.cancelOrderByOrderNo(user.getId(),orderNo);
    }










    @RequestMapping("/pay.do")
    public ServiceResponse pay(Long orderNo, HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
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
        User user = (User)request.getSession().getAttribute("user");
        return iOrderService.getOrderPayStatus(orderNo);
    }
}
