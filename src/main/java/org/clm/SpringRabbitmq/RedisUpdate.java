package org.clm.SpringRabbitmq;

import com.alipay.api.AlipayResponse;
import com.rabbitmq.client.Channel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.clm.Dao.*;
import org.clm.Pojo.Cart;
import org.clm.Pojo.Order;
import org.clm.Pojo.OrderItem;
import org.clm.Pojo.Product;
import org.clm.VO.OrderItemVo;
import org.clm.VO.OrderVo;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.clm.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.data.repository.init.ResourceReader.Type.JSON;

/**
 * @author Ccc
 * @date 2018/10/21 0021 下午 3:52
 */
public class RedisUpdate implements ChannelAwareMessageListener{

    private static Logger log = LoggerFactory.getLogger(RedisUpdate.class);
    @Autowired
    private RedisTemplateUtil redisTemplateUtil;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {


        //channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        //拒绝消息
        //channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

        String s = new String(message.getBody());
        Map map = JsonUtil.StringToObj(s,Map.class);
        //Order orderMessage = JsonUtil.StringToObj(strJson,Order.class);
        Integer userId = (Integer) map.get("userId");
        Integer shippingId = (Integer) map.get("shippingId");
        Long key = (Long) map.get("key");

        /**一辆车一种商品*/
        List<Cart> cartList = cartMapper.selectCartCheckedByUserId(userId);

        /**生成对应的商品详情*/
        ServiceResponse response = this.getOrderItemList(cartList);
        List<OrderItem> orderItemList = (List<OrderItem>) response.getData();
        if(CollectionUtils.isEmpty(orderItemList)){
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            return;
        }
        /**所有商品总价格*/
        BigDecimal payment = getOrderTotalPrice(orderItemList);

        /**生成订单号并设置参数 参数:userId,shippingId,payment总价格 */
        Order order = this.createorder(userId,shippingId,payment);
        if(order==null){
        }

        /**商品详情表设置订单号*/
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }

        //mybatis 批量插入
        int i = orderItemMapper.batchInsert(orderItemList);
        if (i==0){
            //ack返回false，此条mq并重新回到队列
            //deliveryTag:该消息的index
            //requeue：被拒绝的是否重新入队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
        //减少产品库存
        boolean result = this.reduceOrIncreaseProductStock(orderItemList, true);
        if(result){
            //确认当前消息,mq队列移除该消息
            //deliveryTag:该消息的index
            //multiple：是否批量.true:将一次性ack所有小于deliveryTag的消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            //清空一下购物车
            this.clearCart(cartList);
        }




        //返回给前端数据
        OrderVo orderVo = this.createOrderVo(order,orderItemList);
        //把订单信息对象存放到redis缓存中
        redisTemplateUtil.setEx(Const.objType.ORDER,""+userId+shippingId+key,orderVo,Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
    }

    //Routingkey productDetail.update
    public void updateProductRedis(String proJsonStr){
        Product product = JsonUtil.StringToObj(proJsonStr, Product.class);
        log.info("\n============================获取消息.修改产品信息\n{}\n",product.getId());
        try {
            //删除产品详情在redis中的缓存
            redisTemplateUtil.del(Const.objType.PRODUCT, "" + product.getId());
            //如果修改了价格
            //删除商品列表详情缓存
            redisTemplateUtil.delByKey(Const.objType.PRODOCTLISTVO, "search");
            redisTemplateUtil.delByKey(Const.objType.PRODOCTLISTVO,""+product.getCategoryId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Routingkey user.update
    public void updatrUserRedis(String sessionID){

        log.info("\n============================获取消息.修改用户信息\n{}\n",sessionID);
        try {
            redisTemplateUtil.del(Const.objType.SESSION,""+sessionID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Routingkey stock.update
    public void updateProductStock(String orderItemJsonStr){
        OrderItem orderItem = JsonUtil.StringToObj(orderItemJsonStr, OrderItem.class);
        log.info("\n=========================获取消息.修改库存\n{}\n",orderItem.getProductId());

        //删除产品详情在redis中的缓存
        try {
            redisTemplateUtil.del(Const.objType.PRODUCT, "" + orderItem.getProductId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object clearCart(List<Cart> cartList) {
        if(cartList==null){
            return "该购物车为空";
        }
        for (Cart cart : cartList) {
            if (cart!=null){
                cartMapper.deleteByPrimaryKey(cart.getId());
            }
        }
        return "购物车已清空";
    }

    private ServiceResponse getOrderItemList(List<Cart> cartList){
        List<OrderItem> orderItemList = new ArrayList<>();
        for (Cart cart : cartList) {
            /**校验购物车中产品状态及库存*/
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
                return ServiceResponse.createByErrorMessage("商品已下架");
            }

            if (product.getStock()<cart.getQuantity()){
                return ServiceResponse.createByErrorMessage("库存不足");
            }
            /**添加orderItem*/
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(cart.getUserId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServiceResponse.createBySucces(orderItemList);
    }

    /**计算订单总价格*/
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    /**随机生成订单号*/
    private long generateOrderNo(){
        long currentTime =System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

    /**生成订单*/
    private Order createorder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        /*order.setCreateTime();*/
        /* order.setPaymentTime();*/
        int resultCow = orderMapper.insertSelective(order);
        if(resultCow > 0){
            return order;
        }
        return null;
    }

    /**减少或增加库存 true减少,fasle返回增加*/
    private boolean reduceOrIncreaseProductStock(List<OrderItem> orderItemList,boolean reduceOrIncrease) {

        for (OrderItem orderItem : orderItemList) {
            //获取产品库存redisTemplateUtil.get(Const.objType.PRODUCT, "" + orderItem.getProductId());
            Product product = null;
            if (product==null){
                product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            }
            if (reduceOrIncrease){
                //将库存持久化到数据库
                if ( product.getStock()-orderItem.getQuantity()>=0){
                    int i = productMapper.reductStock(product.getId(), product.getStock(), orderItem.getQuantity());
                    while (i != 1){
                        product = productMapper.selectByPrimaryKey(orderItem.getProductId());
                        if ( product.getStock()-orderItem.getQuantity()>=0) {
                            i = productMapper.reductStock(product.getId(), product.getStock(), orderItem.getQuantity());
                            log.info("→数据不一致,重新更新");
                        }else {
                            i =1 ;
                        }
                    }
                }
            }else{
                int i = productMapper.addStock(product.getId(), product.getStock(), orderItem.getQuantity());
                while (i != 1){
                    product = productMapper.selectByPrimaryKey(orderItem.getProductId());
                    i =  productMapper.addStock(product.getId(), product.getStock(), orderItem.getQuantity());
                }
            }

            redisTemplateUtil.del(Const.objType.PRODUCT,""+orderItem.getProductId());

            //发送消息队列
//          rabbitTemplate.convertAndSend(Const.Routingkey.STOCKUPDATE,JsonUtil.objToString(orderItem));
//          redisTemplateUtil.del(Const.objType.PRODUCT,""+orderItem.getProductId());

        }
        return true;
    }

    private OrderVo createOrderVo(Order order, List<OrderItem> orderItemList) {
        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order,orderVo,new String[]{"id","userId","shippingId","updateTime"});

        /**将orderItemList复制到orderItemVoList*/
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = new OrderItemVo();

            BeanUtils.copyProperties(orderItem,orderItemVo,new String[]{"id","userId","updateTime"});
            orderItemVoList.add(orderItemVo);
        }
        /**转换时间*/
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setShippingId(order.getShippingId());
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        String ReceiverName = shippingMapper.selectReceiverNameByShippingId(order.getShippingId());
        /**收货人*/
        orderVo.setReceiverName(ReceiverName);

        /*orderVo.setShippingVo(null);*/
        return orderVo;
    }

}
