package org.clm.util;

import com.github.pagehelper.PageInfo;
import org.clm.VO.ProductListVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
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
        key = objType+"_"+key;
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
        key = objType+"_"+key;
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
        key = objType+"_"+key;
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
        key = objType+"_"+key;
        try {
            /*value = JsonUtil.objToString(obj);*/
            redisTemplate.opsForValue().set(key,obj);
            return true;
        }catch (Exception e){
            log.info("\n=========set:errer==========\n{}\n",e);
            return false;
        }
    }

    public <T> boolean setEx(String objType, String key,T obj,int timeout){
        key = objType+"_"+key;
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
        key = objType+"_"+key;
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
        key = objType+"_"+key;
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
        key = objType+"_"+key;
        try {
            /*value = JsonUtil.objToString(obj);*/
            redisTemplate.delete(redisTemplate.keys(key));
            return true;
        }catch (Exception e){
            log.info("\n=========del:errer==========\n{}\n",e);
            return false;
        }
    }

    public boolean expire(String objType, String key, int timeout) {
        key = objType+"_"+key;
        try {
            redisTemplate.expire(key,timeout, TimeUnit.SECONDS);
            return true;
        }catch (Exception e){
            log.info("\n=========del:errer==========\n{}\n",e);
            return false;
        }
    }
}
