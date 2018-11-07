package org.clm.util;

import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.clm.Pojo.Product;
import org.clm.VO.ProductListVo;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.connection.ConnectionUtils;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Ccc
 * @date 2018/10/12 0012 下午 10:06
 */
@Component
public class RedisTemplateUtil {

    private static Logger log = LoggerFactory.getLogger(RedisTemplateUtil.class);

    @Autowired
    private  RedisTemplate redisTemplate;

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    public RedisTemplate getRedisTemplate(){
        return redisTemplate;
    }

    public  <T> boolean lset(String objType, String key, List<T> objList){
        key = objType+""+key;
        try {
            /*value = JsonUtil.objToString(obj);*/
             redisTemplate.opsForList().leftPush(key,objList);
             return true;
        }catch (Exception e){
            log.info("\n=========lset:errer{}==========\n",e);
            return false;
        }
    }
    public  boolean lsetx(String objType, String key, List objList){
        key = objType+""+key;
        try {
            /*value = JsonUtil.objToString(obj);*/
            redisTemplate.opsForList().leftPush(key,objList);
            return true;
        }catch (Exception e){
            log.info("\n=========lset:errer{}==========\n",e);
            return false;
        }
    }


    public <T> List<T>  lget(String objType, String key ,T obj){
        key = objType+""+key;
        List<T> dataList = null;
        try {
            /*value = JsonUtil.objToString(obj);*/
            List<Page> range = redisTemplate.opsForList().range(key, 0, -1);
            dataList = (List<T>)range.get(0);
            return dataList;
        }catch (Exception e){
            log.info("\n=========lget:errer==========\n{}\n",e);
            return null;
        }
    }


    public <T> boolean set(String objType, String key,T obj){
        key = objType+""+key;
        try {
            if (obj!=null){
                redisTemplate.opsForValue().set(key,obj);
            }else {
                String value = "noExits";
                //缓存雪崩,设置一个值,30秒后失效
                redisTemplate.opsForValue().set(key,value,60,TimeUnit.SECONDS);
            }
            /*value = JsonUtil.objToString(obj);*/
            return true;
        }catch (Exception e){
            log.info("\n=========set:errer==========\n{}\n",e);
            return false;
        }
    }

//    public <T> boolean set(String objType, String key,T obj){
//        key = objType+""+key;
//        try {
//            /*value = JsonUtil.objToString(obj);*/
//            redisTemplate.opsForValue().setNX(key,obj);
//            return true;
//        }catch (Exception e){
//            log.info("\n=========set:errer==========\n{}\n",e);
//            return false;
//        }
//    }

    public <T> boolean setEx(String objType, String key,T obj,int timeout){
        key = objType+""+key;
        try {
            /*value = JsonUtil.objToString(obj);*/
            redisTemplate.opsForValue().set(key,obj,timeout,TimeUnit.SECONDS);
            return true;
        }catch (Exception e){
            log.info("\n=========set:errer==========\n{}\n",e);
            return false;
        }
    }

    public <T> T get(String objType, String key){
        key = objType+""+key;
        try {
            /*value = JsonUtil.objToString(obj);*/
            T data = (T)redisTemplate.opsForValue().get(key);
            return data;
        }catch (Exception e){
            log.info("\n=========set:errer==========\n{}\n",e);
            return null;
        }
    }

    public <T> boolean del(String objType, String key){
        key = objType+""+key;
        try {
            /*value = JsonUtil.objToString(obj);*/
            redisTemplate.delete(key);
            return true;
        }catch (Exception e){
            log.info("\n=========del:errer==========\n{}\n",e);
            return false;
        }
    }

    public <T> boolean delByKey(String objType, String key){
        key = objType+""+key;
        try {
            Set keys = redisTemplate.keys(key + "*");
            /*value = JsonUtil.objToString(obj);*/
            redisTemplate.delete(redisTemplate.keys(key+"*"));
            redisTemplate.exec();

            return true;
        }catch (Exception e){
            log.info("\n=========delByKey:errer==========\n{}\n",e);
            return false;
        }
    }

    public boolean expire(String objType, String key, int timeout) {
        key = objType+""+key;
        try {
            redisTemplate.expire(key,timeout, TimeUnit.MILLISECONDS);
            return true;
        }catch (Exception e){
            log.info("\n=========expire:errer==========\n{}\n",e);
            return false;
        }
    }

    public <T> boolean setNX(String objType, String key,T obj) {
        key = objType+""+key;
        Boolean reulst = false;

        try {
            reulst = redisTemplate.opsForValue().setIfAbsent(key, obj);
            return reulst;
        }catch (Exception e){
            log.info("\n=========setNX:errer==========\n{}\n",e);
            return reulst;
        }finally {
            redisTemplate.getConnectionFactory().getConnection().close();
        }
    }
    public <T> T getset(String objType, String key,T obj) {
        key = objType+""+key;
        T result = null;
        try {
            result = (T)redisTemplate.opsForValue().getAndSet(key, obj);
            return result;
        }catch (Exception e){
            log.info("\n=========getset:errer==========\n{}\n",e);
            return result;
        }
    }
    public Boolean haskey(String objType, String key) {
        key = objType+""+key;
        Boolean result = false;
        try {
            result = redisTemplate.hasKey(key);
            return result;
        }catch (Exception e){
            log.info("\n=========haskey:errer==========\n{}\n",e);
            return result;
        }
    }

}
