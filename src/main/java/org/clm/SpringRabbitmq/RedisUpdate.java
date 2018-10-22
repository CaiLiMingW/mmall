package org.clm.SpringRabbitmq;

import org.clm.Pojo.OrderItem;
import org.clm.Pojo.Product;
import org.clm.common.Const;
import org.clm.util.JsonUtil;
import org.clm.util.RedisTemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Ccc
 * @date 2018/10/21 0021 下午 3:52
 */
public class RedisUpdate {


    private static Logger log = LoggerFactory.getLogger(RedisUpdate.class);
    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

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


}
