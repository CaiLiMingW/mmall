package org.clm.util.bak;

import org.clm.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Ccc
 * @date 2018/10/12 0012 下午 9:28
 */
public class RedisPool {
    /**Sharded连接池*/
    private static JedisPool pool;
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

    private static String redisIp1 = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redisPort1 = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));


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
        /*info1.setPassword("cwssz");*/
        /*info1.setPassword();设置密码*/
        /*info2.setPassword("cwssz");*/

        pool= new JedisPool(config,redisIp1,redisPort1);
    }

    /**
     * 连连接池获取jedis
     * @return
     */
    public static Jedis getJedis(){
        return pool.getResource();
    }

    /**
     * 回收jedis
     * @param jedis
     */
    public static void returnBrokenResources(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    /**
     * 回收损坏的jedis
     * @param jedis
     */
    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

}
