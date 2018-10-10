package org.clm.common;

import org.clm.util.PropertiesUtil;
import org.clm.util.RedisPoolUtil;
import org.junit.rules.Timeout;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Ccc
 * @date 2018/10/9 0009 下午 7:38
 */
public class RedisPool {
    /**jedis连接池*/
    private static JedisPool jedisPool ;
    /**最大连接数*/
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));
    /**最大空闲状态的jedis实例个数*/
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));
    /**最小空闲状态的jedis实例个数*/
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2"));
    /**在borrow一个实例时，是否进行验证操作*/
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));;
    /**在Return一个实例时，是否进行验证操作*/
    private static Boolean testOnRetun =Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","false")) ;

    private static String redisIp = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    static {
        initPool();
    }

    private  static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnRetun);

        /**连接耗尽时是否阻塞，false抛出异常，true阻塞直到超时*/
        config.setBlockWhenExhausted(true);

        jedisPool = new JedisPool(config,redisIp,redisPort,1000*2);
    }

    /**
     * 连连接池获取jedis
     * @return
     */
    public static Jedis getJedis(){
        return jedisPool.getResource();
    }

    /**
     * 回收jedis
     * @param jedis
     */
    public static void returnBrokenResources(Jedis jedis){
            jedisPool.returnBrokenResource(jedis);
    }

    /**
     * 回收损坏的jedis
     * @param jedis
     */
    public static void returnResource(Jedis jedis){
            jedisPool.returnResource(jedis);
    }

    public static void main(String[] args) {
        String set = RedisPoolUtil.setEx("cwzzex", "60", 60);
        RedisPoolUtil.set("cwzzexprie", "ss");
        RedisPoolUtil.expire("cwzzexprie",60);
        System.out.println("插入结果:"+set);
        String s = RedisPoolUtil.get("cwzz");
        System.out.println("查询结果:"+s);
    }


}
