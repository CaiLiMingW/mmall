package org.clm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;
import sun.rmi.runtime.Log;

/**
 * @author Ccc
 * @date 2018/10/12 0012 下午 10:06
 */
public class ShardedPoolUtil {
    private static Logger log = LoggerFactory.getLogger(ShardedPoolUtil.class);
    public static String set(String key,String value){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis =  RedisShardedPool.getJedis();
            result = jedis.set(key, value);
        }catch (Exception e){
            RedisShardedPool.returnBrokenResources(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String setEx(String key,String value,int exTime){
        ShardedJedis jedis = null;
        String result = null;
        log.info("\n=========setEx==========\n");
        try {
            jedis =  RedisShardedPool.getJedis();
            log.info("\n=======获取jedis==========\n",jedis);
            result = jedis.setex(key, exTime,value);
        }catch (Exception e){
            RedisShardedPool.returnBrokenResources(jedis);
            log.info("\n========获取jedis失败=========\n");
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long expire(String key,int exTime){
        ShardedJedis jedis = null;
        Long result = null;
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

    public static String get(String key){
        ShardedJedis jedis = null;
        String result = null;
        log.info("\n=========get==========\n");
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
            log.info("\n=======jedis.get(),key===========\n{}\n",key);
        }catch (Exception e){
            RedisShardedPool.returnBrokenResources(jedis);
            log.info("\n=======jedis异常===========\n{}\n",jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long del(String   key){
        ShardedJedis jedis = null;
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
        for (int i = 0; i <20 ; i++) {
            ShardedPoolUtil.del("key"+i);
        }
    }
}
