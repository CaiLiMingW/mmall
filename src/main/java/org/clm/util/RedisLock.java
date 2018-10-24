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
            boolean expire = redisTemplateUtil.expire(Const.objType.LOCK, key, TIMEOUT);
        }else {
            Long lockValue1 = redisTemplateUtil.get(Const.objType.LOCK, key);
            //死锁
            if (lockValue1 != null && System.currentTimeMillis() > lockValue1){
                log.info("\n========发现死锁{}=======",Thread.currentThread().getId());
                Long lockValue2 = redisTemplateUtil.getset(Const.objType.LOCK, key, System.currentTimeMillis() + TIMEOUT);

                //
                if (lockValue2 != null || lockValue1.equals(lockValue2)){
                    redisTemplateUtil.expire(Const.objType.LOCK,key,TIMEOUT);
                    log.info("\n========{}:重置生存时间=======",Thread.currentThread().getId());
                }else {
                    log.info("\n!=======lockValue1.equals(lockValue2)=======");
                    return false;
                }

            }else {
                log.info("\n=======System.currentTimeMillis() < lockValue1=======");
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
