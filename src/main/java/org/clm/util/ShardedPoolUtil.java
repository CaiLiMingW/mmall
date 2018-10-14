package org.clm.util;

import redis.clients.jedis.ShardedJedis;

/**
 * @author Ccc
 * @date 2018/10/12 0012 下午 10:06
 */
public class ShardedPoolUtil {

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
        try {
            jedis =  RedisShardedPool.getJedis();
            result = jedis.setex(key, exTime,value);
        }catch (Exception e){
            RedisShardedPool.returnBrokenResources(jedis);
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
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
        }catch (Exception e){
            RedisShardedPool.returnBrokenResources(jedis);
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
