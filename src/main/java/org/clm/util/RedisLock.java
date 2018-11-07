package org.clm.util;

import org.apache.commons.lang3.StringUtils;
import org.clm.common.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.runtime.Log;

import java.sql.Time;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Ccc
 * @date 2018/10/24 0024 上午 9:23
 */
@Component
public class RedisLock {
    private static final Logger log = LoggerFactory.getLogger(RedisLock.class);

    private static final int TIMEOUT = 800;
    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    public boolean setLock(String key){

        boolean b = redisTemplateUtil.setNX(Const.objType.LOCK, key, System.currentTimeMillis() + TIMEOUT);
        if (b){
            Lock lock;
            ReentrantLock reentrantLock;
            boolean expire = redisTemplateUtil.expire(Const.objType.LOCK, key, TIMEOUT);
        }else {
            Long lockValue1 = redisTemplateUtil.get(Const.objType.LOCK, key);
            //一重检查:锁存在并且时间过期已失效,可以进入修改该锁
            if (lockValue1 != null && System.currentTimeMillis() > lockValue1){
                log.info("\n========第一次检查:锁已失效,进入修改{}=======",Thread.currentThread().getId());
                //重新设置锁时间 getset:原子性操作
                Long lockValue2 = redisTemplateUtil.getset(Const.objType.LOCK, key, System.currentTimeMillis() + TIMEOUT);

                //二重检查：getset前锁消失：null 该锁未被其他进程使用
                if (lockValue2 == null || lockValue1.equals(lockValue2)){
                    redisTemplateUtil.expire(Const.objType.LOCK,key,TIMEOUT);
                    log.info("\n========{}第二次检查::重置锁时间并使用该锁=======",Thread.currentThread().getId());
                }else {
                    log.info("\n=======没有获取分布式锁,被其他进程使用=======");
                    return false;
                }

            }else {
                log.info("\n=======,锁未过期,被其他进程使用无权使用=======");
                return false;
            }

        }
        return true;
    }

    public boolean delLock(String key){
        boolean del = redisTemplateUtil.del(Const.objType.LOCK, key);
        if (del){
            return true;
        }else {
            return false;
        }
    }

    public boolean getLockValue(){

        Long lockValue1 = redisTemplateUtil.get(Const.objType.LOCK, "");
        if (lockValue1 != null && System.currentTimeMillis() > lockValue1){
            Long lockValue2 = redisTemplateUtil.getset(Const.objType.LOCK, "", System.currentTimeMillis() + TIMEOUT);
            if (lockValue2 != null || lockValue1 == lockValue2){

            }
        }
        return true;
    }

}
