package org.clm.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Ccc
 * @date 2018/9/27 0027 下午 3:35
 */
public class TokenCache {

    private static Logger log = LoggerFactory.getLogger(TokenCache.class);

    //LRU算法
    private static LoadingCache<String,String> loaclCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setkey(String key,String value){
         loaclCache.put(key,value);
    }

    public static String getkey(String key){
        String value = null;
        try {
            value = loaclCache.get(key);
            if ("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            log.error("localCache get error",e);
        }
        return null;
    }
}
