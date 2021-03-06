package org.clm.common;


import org.apache.commons.lang3.StringUtils;

/** 常态常量
 * @author Ccc
 * @date 2018/9/27 0027 上午 10:34
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public class Routingkey {
        public static final String PRODUCTUPDATE = "productDetail.update";
        public static final String USERUPDATE = "user.update";
        public static final String STOCKUPDATE = "stock.update";
        public static final String ORDERMESSAGE = "order.message";
    }

    public class objType{
        public static final String CART = "Cart";
        public static final String CATRGORY = "Category";
        public static final String ORDER = "Order";
        public static final String PAYINFO = "Payinfo";
        public static final String PRODUCT = "Product";
        public static final String SHIPPING = "Shipping";
        public static final String USER = "User";
        public static final String SESSION = StringUtils.EMPTY;
        public static final String PRODOCTLISTVO = "productListVos";
        public static final String LOCK = "lock";


    }
    public interface RedisCacheExtime{
        int REDIS_SESSION_EXTIME = 60*30*1000;
        int REDIS_LOCK_EXTIME = 500;
    }

    public interface OrderBy{
        String PRICE_DESC = "price DESC";
        String PRICE_ASC  = "price ASC";
        String NULL  = StringUtils.EMPTY;
    }

    public interface Cart{
        int CHECKED = 1;//即购物车选中状态
        int UN_CHECKED = 0;//购物车中未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }
    public enum ProductStatusEnum{
        ON_SALE(1,"在线");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public interface Role{
        /**普通用户*/
        int ROLE_CUSTOMER = 0;
        /**管理员*/
        int ROLE_ADMIN = 1;

    }
    /**订单状态枚举*/
    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");


        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum : values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("么有找到对应的枚举");
        }
    }
    /***/
    public interface  AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }
    /**支付平台*/
    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        PayPlatformEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");

        PaymentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }


        public static PaymentTypeEnum codeOf(int code){
            for(PaymentTypeEnum paymentTypeEnum : values()){
                if(paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

    }
}
