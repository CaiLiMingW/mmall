package org.clm.util.bak;/*

package org.clm.util;

import org.clm.common.RedisPool;
import redis.clients.jedis.Jedis;


*/
/** API封装调试
 * @author Ccc
 * @date 2018/10/9 0009 下午 8:29
 *//*


public class RedisPoolUtil {


*/
/**
     * 重新设置key 有效期 单位秒
     * @param key
     * @param exTime
     * @return
     *//*


    public static Long expire(String key,int exTime){
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key,exTime);
        } catch (Exception e) {
            RedisPool.returnBrokenResources(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }



*/
/**
     * session封装set get方法
     * @param key
     * @param value
     * @param exTime session过期时间 单位秒
     * @return
     *//*


    public static String setEx(String key,String value,int exTime){
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key,exTime,value);
        } catch (Exception e) {
            RedisPool.returnBrokenResources(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String set(String key,String value){
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key,value);
        } catch (Exception e) {
            RedisPool.returnBrokenResources(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            RedisPool.returnBrokenResources(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }


*/
/**
     * 删除key
     * @param key
     * @return
     *//*


    public static Long del(String key){
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            RedisPool.returnBrokenResources(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }
}

*/
