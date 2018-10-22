package org.clm.Service.Impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.ItemOrderVO;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mchange.v1.db.sql.ConnectionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.clm.Dao.*;
import org.clm.Pojo.*;
import org.clm.Service.IOrderService;
import org.clm.VO.OrderItemVo;
import org.clm.VO.OrderProductVo;
import org.clm.VO.OrderVo;
import org.clm.VO.ShippingVo;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.clm.util.*;
import org.joda.time.DateTimeUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.OptionalDataException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Ccc
 * @date 2018/10/5 0005 下午 10:55
 */
@Service
public class OrderServiceImpl implements IOrderService {

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;
    static {

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private RedisTemplateUtil redisTemplateUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static Log log = LogFactory.getLog(OrderServiceImpl.class);

    @Override
    public ServiceResponse pay(Long orderNo, Integer userId, String path) {
        Map map = new HashMap();
        Order order = orderMapper.selectByOrderByIdAndUserId(orderNo, userId);
        if(order == null){
            return ServiceResponse.createByErrorMessage("用户没有该订单");
        }
        map.put("orderNo",order.getOrderNo());


        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
        String subject = new StringBuffer().append("clm商城支付").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();


        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0.0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
        String body = new StringBuffer().append("订单").append(outTradeNo).append(totalAmount).toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        String providerId = "2088100200300400500";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItems = orderItemMapper.selectByUserIdAndOrderNo(order.getUserId(),order.getOrderNo());
        for (OrderItem orderItem : orderItems) {
            GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                     BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),
                     orderItem.getQuantity());
            goodsDetailList.add(goodsDetail);

        }
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
       /* GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx面包", 1000, 1);*/
        // 创建好一个商品后添加至商品明细列表
       /* goodsDetailList.add(goods1);*/

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
        /*GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
        goodsDetailList.add(goods2);*/

        String appAuthToken = "应用授权令牌";//根据真实值填写

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                /*支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置*/
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url","http://120.78.128.136/order/alipay_callback.do"))
                .setGoodsDetailList(goodsDetailList);

        // 调用tradePay方法获取当面付应答
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                /**检查文件夹*/
                File folder = new File(path);
                if (!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                // 需要修改为运行机器上的路径 注意加/  "path/+qr-response.getOutTradeNo().png"
                String qrPath = String.format(path+"/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("/qr-%s.png", response.getOutTradeNo());
                //生成二维码图片
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                //                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                /**将二维码图片(qrFileName)文件上传path*/
                File targetFile = new File(path,qrFileName);
                try {
                    boolean b = FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    log.error("上传二维码异常",e);
                }
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
                map.put("qrUrl",qrUrl);
                log.info("上传的文件路径:\\n" + qrPath);

                return ServiceResponse.createBySucces(map);
            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServiceResponse.createByErrorMessage("支付宝预下单失败!!!");
            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServiceResponse.createByErrorMessage("系统异常，预下单状态未知!!!");
            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServiceResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    @Override
    public ServiceResponse alipayCallback(HttpServletRequest request) {
        Map<String,String> params = Maps.newHashMap();

        //获取返回的参数map
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        /**
         * s：key值
         * values：键值数组
         */
        for (String s : requestParameterMap.keySet()) {
            String[] values = requestParameterMap.get(s);
            String valueStr = "";
            for(int i = 0;i < values.length;i++){
                valueStr = (i==values.length-1)?valueStr + values[i]:valueStr + values[i] + ",";
            }
            params.put(s,valueStr);
        }
      /*  for (Iterator iter = parameterMap.keySet().iterator() ;iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) parameterMap.get(name);
        }*/

        /**验证回调的正确性,是不是支付宝发的.避免重复通知.**/
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

            if(!alipayRSACheckedV2){
                return ServiceResponse.createByErrorMessage("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常",e);
        }

        //todo 验证各种数据

        /**原支付请求的商户订单号 */
        Long orderNo =  Long.parseLong(params.get("out_trade_no"));

        /**交易目前所处的状态 */
        String tradeStatus = params.get("trade_status");

        /**支付宝交易凭证号 */
        String tradeNo = params.get("trade_no");

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServiceResponse.createByErrorMessage("该订单不存在，回调忽略");
        }

        /**判断订单状态,是否重复回调*/

        if(order.getStatus()>= Const.OrderStatusEnum.PAID.getCode()){
            ServiceResponse.createBySuccessMessage("支付回调重复调用,过滤");
        }
        log.info("\n=========判断订单状态==========\n");

        /**校验订单状态，是否已成功*/
       if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
           order.setPaymentTime(DataTimeUtil.strToDate(params.get("gmt_payment")));
           order.setStatus(Const.OrderStatusEnum.PAID.getCode());
           int updateResult = orderMapper.updateByPrimaryKeySelective(order);
       }
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insertSelective(payInfo);

        return ServiceResponse.createBySuccess();
    }

    @Override
    public ServiceResponse getOrderPayStatus(Long orderNo) {
        if (orderNo==null){
            return ServiceResponse.createByErrorMessage("订单号为空");
        }

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServiceResponse.createByErrorMessage("用户没有该订单,查询失败");
        }
        /**判断订单支付状态,如果已支付:*/
        if (order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()){
            return ServiceResponse.createBySucces(true);
        }
        return ServiceResponse.createBySucces(false);
    }


    /**
     * 流程: userId,shippingId → Cart → OrderItem → Order
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServiceResponse createOrder(Integer userId, Integer shippingId) {

        /**一辆车一种商品*/
        List<Cart> cartList = cartMapper.selectCartCheckedByUserId(userId);

        /**生成对应的商品详情*/
        ServiceResponse response = this.getOrderItemList(cartList);
        List<OrderItem> orderItemList = (List<OrderItem>) response.getData();
        if(CollectionUtils.isEmpty(orderItemList)){
            return ServiceResponse.createByErrorMessage("购物车为空");
        }
        /**所有商品总价格*/
        BigDecimal payment = getOrderTotalPrice(orderItemList);

        /**生成订单号并设置参数 参数:userId,shippingId,payment总价格 */
        Order order = this.createorder(userId,shippingId,payment);
        if(order==null){
            return ServiceResponse.createByErrorMessage("生成订单错误");
        }

        /**商品详情表设置订单号*/
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }

        //mybatis 批量插入
        orderItemMapper.batchInsert(orderItemList);

        //减少产品库存
        boolean result = this.reduceOrIncreaseProductStock(orderItemList, true);
        if(!result){
            return ServiceResponse.createByErrorMessage("库存不足");
        }
        //清空一下购物车
//        this.clearCart(cartList);

        //返回给前端数据
        OrderVo orderVo = this.createOrderVo(order,orderItemList);
        return ServiceResponse.createBySucces(orderVo);
    }

    @Override
    public ServiceResponse getOrderProduct(Integer userId) {
        /**返回视图VO类默认为空*/
        OrderProductVo orderProductVo = new OrderProductVo();
        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        BigDecimal payment = new BigDecimal("0");

        /**通过userId获取购物车*/
        List<Cart> cartList = cartMapper.selectCartCheckedByUserId(userId);

        /***/
        ServiceResponse response = this.getOrderItemList(cartList);
        if (!response.isSuccess()){
            return response;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) response.getData();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = new OrderItemVo();
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            BeanUtils.copyProperties(orderItem,orderItemVo,new String[]{"id","userId","updateTime"});
            orderItemVoList.add(orderItemVo);
        }
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        orderProductVo.setProductTotalPrice(payment);
        return ServiceResponse.createBySucces(orderProductVo);
    }

    @Override
    public ServiceResponse<PageInfo> getOrderListByUserId(Integer userId,Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        /**如果userId为NULL，管理员操作*/

        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderItem> orderItemList = orderItemMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = new ArrayList<>();
        for (Order order : orderList) {
            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderItem orderItem : orderItemList) {
                if (order.getOrderNo().equals(orderItem.getOrderNo())){
                    orderItems.add(orderItem);
                }
            }

           orderVoList.add(this.createOrderVo(order,orderItems));
        }
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);

        return ServiceResponse.createBySucces(pageInfo);
    }

    @Override
    public ServiceResponse getOrderDeatilByOrderNo(Integer userId, Long orderNo) {
        if(orderNo==null){
            return ServiceResponse.createByErrorMessage("订单号为空");
        }
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServiceResponse.createByErrorMessage("没有找到订单");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId, orderNo);
        OrderVo orderVo = this.createOrderVo(order, orderItemList);

        /**设置地址详情*/
        ShippingVo shippingVo = new ShippingVo();
        Shipping shipping = shippingMapper.selectByPrimaryKey(orderVo.getShippingId());
        BeanUtils.copyProperties(shipping,shippingVo,new String[]{"id","userId0","createTime","updateTime"});
        orderVo.setShippingVo(shippingVo);

        return ServiceResponse.createBySucces(orderVo);
    }

    @Override
    public ServiceResponse cancelOrderByOrderNo(Integer useId, Long orderNo) {
        if(orderNo==null){
            return ServiceResponse.createByErrorMessage("订单号为空");
        }
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServiceResponse.createByErrorMessage("该用户没有此订单");
        }
        if(order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()){
            return ServiceResponse.createByErrorMessage("此订单已付款，无法被取消");
        }
        order.setStatus(0);
        int resultCow = orderMapper.closeOrderByOrderNo(order.getOrderNo());

        if (resultCow>0){


            //获取订单详情
            List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(useId, orderNo);
            this.reduceOrIncreaseProductStock(orderItemList,false);
//            for (OrderItem orderItem : orderItemList) {
//                //从缓存中获取旧的产品库存
//                Product product = redisTemplateUtil.get(Const.objType.PRODUCT,""+orderItem.getProductId());
//                if (product==null){
//                    product = productMapper.selectByPrimaryKey(orderItem.getProductId());
//                }
//                //返回库存
//                product.setStock(product.getStock()+orderItem.getQuantity());
//                //持久化到数据库
//                int update = productMapper.updateByPrimaryKeySelective(product);
//                if (update > 0){
//                    //删除缓存
//                    redisTemplateUtil.del(Const.objType.PRODUCT,""+product.getId());
//                }
//            }

            return ServiceResponse.createBySuccess();
        }
        return ServiceResponse.createByErrorMessage("订单取消失败");
    }

    @Override
    public ServiceResponse searchByOrderNoKey(Integer pageNum, Integer pageSize, Long orderNo) {
        PageHelper.startPage(pageNum,pageSize);

        List<OrderVo> orderVoList = orderMapper.selectOrderVoByKey(orderNo);
        for (OrderVo orderVo : orderVoList) {
            /**设置地址详情*/
            ShippingVo shippingVo = new ShippingVo();
            Shipping shipping = shippingMapper.selectByPrimaryKey(orderVo.getShippingId());
            BeanUtils.copyProperties(shipping,shippingVo,new String[]{"id","userId0","createTime","updateTime"});
            orderVo.setShippingVo(shippingVo);
            orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
            /**收货人*/
            String ReceiverName = shippingMapper.selectReceiverNameByShippingId(orderVo.getShippingId());
            orderVo.setReceiverName(ReceiverName);
            /**订单状态描述*/
            orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(orderVo.getStatus()).getValue());
            /**支付状态描述*/
            orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(orderVo.getPaymentType()).getValue());
            /***/
        }
        PageInfo pageInfo = new PageInfo(orderVoList);


        return ServiceResponse.createBySucces(pageInfo);
    }

    @Override
    public ServiceResponse sendgoods(Long orderNo) {
        if (orderNo==null){
            return ServiceResponse.createByErrorMessage("订单号不存在");
        }
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServiceResponse.createByErrorMessage("该订单不存在");
        }
        if (order.getStatus().equals(Const.OrderStatusEnum.PAID.getCode())){
            order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
            order.setSendTime(new Date());
            order.setEndTime(new Date());
            int updateCow = orderMapper.updateByPrimaryKeySelective(order);
            if (updateCow > 0){
                return ServiceResponse.createBySuccessMessage("发货成功");
            }
            return ServiceResponse.createByErrorMessage("发货失败");
        }
        if(order.getStatus()>Const.OrderStatusEnum.PAID.getCode()){
            return ServiceResponse.createByErrorMessage("该商品已发货");
        }
        return ServiceResponse.createByErrorMessage("该商品未付款");
    }

    @Override
    public void closeOrderTask(int hour) {
        //当前时间的hour前
        Date closeTime = DateUtils.addHours(new Date(),-hour);
        String s = DateTimeUtil.dateToStr(closeTime);
        //查出hour小时前的订单
        List<OrderItem> orderItemList = orderItemMapper.selectOrderByStatusCreatetime(Const.OrderStatusEnum.NO_PAY.getCode(), DateTimeUtil.dateToStr(closeTime));

        for (OrderItem orderItem : orderItemList) {

            //查看该订单的商品
            Product product = redisTemplateUtil.get(Const.objType.PRODUCT,""+orderItem.getProductId());
            if (product==null){
                 product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            }
            //如果商品已被删除
            if(product == null){
                continue;
            }
            product.setStock(product.getStock()+orderItem.getQuantity());

            //取消订单没有被购买,返还库存
            int update = productMapper.updateByPrimaryKeySelective(product);
            if(update>0){
                rabbitTemplate.convertAndSend(Const.Routingkey.STOCKUPDATE,JsonUtil.objToString(orderItem));
//                redisTemplateUtil.del(Const.objType.PRODUCT,""+orderItem.getProductId());
            }

//            Order order = orderMapper.selectByOrderNo(orderItem.getOrderNo());
//            order.setCloseTime(new Date());
//            orderMapper.updateByPrimaryKeySelective(order);
            int i = orderMapper.closeOrderByOrderNo(orderItem.getOrderNo());
            if (i > 0 ){
                log.info("\n=========关闭订单成功============\n");
            }else {
                log.info("\n=========关闭订单失败============\n");
            }

        }
    }

    @Override
    public void dedelCloseOrderTask() {
        Date s7DayAgo = DateUtils.addDays(new Date(), -7);
        //删除七天前，取消状态的订单
        List<Order> orderList = orderMapper.selectByStatusAndCreatetime(Const.OrderStatusEnum.CANCELED.getCode(), s7DayAgo);
        for (Order order : orderList) {
            int i = orderMapper.deletebyOrderNo(order.getOrderNo());
            if (i >0){
                orderItemMapper.deletebyOrderNo(order.getOrderNo());
            }
        }

        Date twoMonsAgo = DateUtils.addMonths(new Date(), -2);
        //删除两个月前的已发货所有订单
        List<Order> orderList2 = orderMapper.selectByStatusAndCreatetime(Const.OrderStatusEnum.SHIPPED.getCode(), twoMonsAgo);
        for (Order order : orderList2) {
            int j = orderMapper.deletebyOrderNo(order.getOrderNo());
            if (j >0){
                orderItemMapper.deletebyOrderNo(order.getOrderNo());
            }
        }
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

    /**清空购物车*/
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


    /** 简单打印应答**/
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
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
             /*product = productMapper.selectByPrimaryKey(orderItem.getProductId());*/
            /*product.setStock(product.getStock()-orderItem.getQuantity());*/

            //获取产品库存
            Product product = redisTemplateUtil.get(Const.objType.PRODUCT, "" + orderItem.getProductId());
            if (product==null){
                product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            }

            if (reduceOrIncrease){
                int i = product.getStock() - orderItem.getQuantity();
                if (i>=0){
                    product.setStock(product.getStock()-orderItem.getQuantity());
                }else {
                    return false;
                }

            }else{
                product.setStock(product.getStock()+orderItem.getQuantity());
            }

            //将库存持久化到数据库
            int update = productMapper.updateByPrimaryKeySelective(product);
            //删除redis商品详情缓存
            if( update > 0){
                rabbitTemplate.convertAndSend(Const.Routingkey.STOCKUPDATE,JsonUtil.objToString(orderItem));
//                redisTemplateUtil.del(Const.objType.PRODUCT,""+orderItem.getProductId());
            }

        }
        return true;
    }

    public static void main(String[] args) {
        Date date = DateUtils.addMonths(new Date(), -1);
        System.out.println(date);
    }
    /***/
}
