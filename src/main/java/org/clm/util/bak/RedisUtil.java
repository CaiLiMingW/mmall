package org.clm.util.bak;

import org.clm.util.JsonUtil;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Ccc
 * @date 2018/10/12 0012 下午 10:06
 */
public class RedisUtil {

    private static Logger log = LoggerFactory.getLogger(RedisUtil.class);
    
    private static JedisPool jedisPool;

    @Autowired
    private static JedisPoolConfig jedisPoolConfig;

   public static Jedis getJedis(){
           return jedisPool.getResource();
   }

    public static <T> String set(String objType,String key,T obj){
        Jedis jedis = null;
        String result = null;
        String value = null;
        key = objType+"_"+key;
        try {
            jedis =  jedisPool.getResource();
            value = JsonUtil.objToString(obj);
            result = jedis.set(key, value);
        }catch (Exception e){
            jedisPool.returnBrokenResource(jedis);
            return result;
        }
        jedisPool.returnResource(jedis);
        return result;
    }

    public  static <T> String setEx(String objType,String key,T obj,int exTime){
        Jedis jedis = null;
        String result = null;
        key = objType+"_"+key;
        try {
            jedis =  jedisPool.getResource();
            String objToString = JsonUtil.objToString(obj);
            result = jedis.setex(key, exTime,objToString);
        }catch (Exception e){
            jedisPool.returnBrokenResource(jedis);
            log.info("\n========获取jedis失败=========\n");
            return result;
        }
        jedisPool.returnResource(jedis);
        return result;
    }

    public static Long expire(String objType,String key,int exTime){
        Jedis jedis = null;
        Long result = null;
        key = objType+"_"+key;
        try {
            jedis =  jedisPool.getResource();
            result = jedis.expire(key,exTime);
        }catch (Exception e){
            jedisPool.returnBrokenResource(jedis);
            return result;
        }
        jedisPool.returnResource(jedis);
        return result;
    }

    public static <T>  T get(String objType,String key,Class<T> clazz){
        Jedis jedis = null;
        key = objType+"_"+key;
        String objStrng = null;
        T data = null;
        log.info("\n=========get==========\n");
        try {
            jedis = jedisPool.getResource();
            objStrng = jedis.get(key);
            log.info("\n=======jedis.get()===========\n{}\n",key);
            data = JsonUtil.StringToObj(objStrng, clazz);
        }catch (Exception e){
            jedisPool.returnBrokenResource(jedis);
            log.info("\n=======jedis异常===========\n{}\n",jedis);
            return null;
        }
        jedisPool.returnResource(jedis);
        return data;
    }
    public static <T>  T getList(String objType, String key, TypeReference<T> typeReference) {
        Jedis jedis = null;
        key = objType + "_" + key;
        String objStrng = null;
        T data = null;
        try {
            jedis = jedisPool.getResource();
            objStrng = jedis.get(key);
            data = JsonUtil.StringToObj(objStrng, typeReference);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            log.info("\n=======jedis.getList异常===========\n{}\n", e);
            return null;
        }
        jedisPool.returnResource(jedis);
        return data;
    }

    public static Long del(String objType,String  key){
        Jedis jedis = null;
        Jedis jedis1= null;
        RedisTemplate redisTemplate;
        key = objType+"_"+key;
        Long result = null;
        try {
            jedis =  jedisPool.getResource();
            result = jedis.del(key);
        }catch (Exception e){
            jedisPool.returnBrokenResource(jedis);
            return result;
        }
        jedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {

   }
}
