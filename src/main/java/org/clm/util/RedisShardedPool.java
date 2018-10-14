package org.clm.util;

import org.apache.commons.collections.PredicateUtils;
import org.clm.Pojo.User;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ccc
 * @date 2018/10/12 0012 下午 9:28
 */
public class RedisShardedPool {
    /**Sharded连接池*/
    private static ShardedJedisPool pool;
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
    private static String redisIp2 = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redisPort2 = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

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
        JedisShardInfo info1 = new JedisShardInfo(redisIp1,redisPort1);
        /*info1.setPassword();设置密码*/
        JedisShardInfo info2 = new JedisShardInfo(redisIp2,redisPort2);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        pool= new ShardedJedisPool(config,jedisShardInfoList, Hashing.MURMUR_HASH, ShardedJedis.DEFAULT_KEY_TAG_PATTERN);
    }

    /**
     * 连连接池获取jedis
     * @return
     */
    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    /**
     * 回收jedis
     * @param jedis
     */
    public static void returnBrokenResources(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    /**
     * 回收损坏的jedis
     * @param jedis
     */
    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        User u = new User();
        u.setId(1);
        u.setUsername("1111");
    }
}
