package org.clm.util.bak;

import org.clm.common.Const;
import org.clm.util.JsonUtil;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

/**
 * @author Ccc
 * @date 2018/10/12 0012 下午 10:06
 */
public class ShardedPoolUtil {
    private static Logger log = LoggerFactory.getLogger(ShardedPoolUtil.class);
    public static <T> String set(String objType,String key,T obj){
        ShardedJedis jedis = null;
        String result = null;
        String value = null;
        key = objType+"_"+key;
        try {
            jedis =  RedisShardedPool.getJedis();
            value = JsonUtil.objToString(obj);
            result = jedis.set(key, value);
        }catch (Exception e){
            RedisShardedPool.returnBrokenResources(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public  static <T> String setEx(String objType,String key,T obj,int exTime){
        ShardedJedis jedis = null;
        String result = null;
        key = objType+"_"+key;
        try {
            jedis =  RedisShardedPool.getJedis();
            String objToString = JsonUtil.objToString(obj);
            result = jedis.setex(key, exTime,objToString);
        }catch (Exception e){
            RedisShardedPool.returnBrokenResources(jedis);
            log.info("\n========获取jedis失败=========\n");
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long expire(String objType,String key,int exTime){
        ShardedJedis jedis = null;
        Long result = null;
        key = objType+"_"+key;
        try {
            jedis =  RedisShardedPool.getJedis();
            result = jedis.expire(key,exTime);
        }catch (Exception e){
            RedisShardedPool.returnBrokenResources(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static <T>  T get(String objType,String key,Class<T> clazz){
        ShardedJedis jedis = null;
        key = objType+"_"+key;
        String objStrng = null;
        T data = null;
        log.info("\n=========get==========\n");
        try {
            jedis = RedisShardedPool.getJedis();
            objStrng = jedis.get(key);
            log.info("\n=======jedis.get()===========\n{}\n",key);
            data = JsonUtil.StringToObj(objStrng, clazz);
        }catch (Exception e){
            RedisShardedPool.returnBrokenResources(jedis);
            log.info("\n=======jedis异常===========\n{}\n",jedis);
            return null;
        }
        RedisShardedPool.returnResource(jedis);
        return data;
    }
    public static <T>  T getList(String objType, String key, TypeReference<T> typeReference) {
        ShardedJedis jedis = null;
        key = objType + "_" + key;
        String objStrng = null;
        T data = null;
        try {
            jedis = RedisShardedPool.getJedis();
            objStrng = jedis.get(key);
            data = JsonUtil.StringToObj(objStrng, typeReference);
        } catch (Exception e) {
            RedisShardedPool.returnBrokenResources(jedis);
            log.info("\n=======jedis.getList异常===========\n{}\n", e);
            return null;
        }
        RedisShardedPool.returnResource(jedis);
        return data;
    }

    public static Long del(String objType,String  key){
        ShardedJedis jedis = null;
        Jedis jedis1= null;
        RedisTemplate redisTemplate;
        key = objType+"_"+key;
        Long result = null;
        try {
            jedis =  RedisShardedPool.getJedis();
            result = jedis.del(key);
        }catch (Exception e){
            RedisShardedPool.returnBrokenResources(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        Long del = ShardedPoolUtil.del(Const.objType.PRODOCTLISTVO, "**");
        System.out.println(del);
    }
}
