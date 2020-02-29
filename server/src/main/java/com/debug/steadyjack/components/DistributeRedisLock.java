package com.debug.steadyjack.components;/**
 * Created by Administrator on 2018/10/24.
 */

import com.google.common.base.Strings;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2018/10/24 20:57
 * @Link:QQ-1974544863
 **/
@Component
public class DistributeRedisLock implements InitializingBean{

    private static final Logger log= LoggerFactory.getLogger(DistributeRedisLock.class);

    @Autowired
    private RedissonClient redissonClient;

    private static Redisson redisson;


    public void afterPropertiesSet() throws Exception {
        redisson= (Redisson) redissonClient;
    }

    /**
     * 获取锁
     * @param lockName
     * @return
     */
    public RLock acquireLock(String lockName){
        RLock lock=redisson.getLock(lockName);
        lock.lock(); //v1

        //lock.lock(10L, TimeUnit.SECONDS); //v2
        //lock.tryLock(110L,10L,TimeUnit.SECONDS);//+v3
        return lock;
    }

    /**
     * 释放锁
     * @param lock
     */
    public void realeaseLock(RLock lock){
        lock.unlock();
    }


    /**
     * 塞入分布式set对象中
     * @param key
     * @param value
     */
    public RSet setKeyValue(final String key,final String value){
        if (!Strings.isNullOrEmpty(key)){
            RSet<String> rSet=redisson.getSet(key);
            rSet.add(value);
            //rSet.expire(24L, TimeUnit.HOURS); //可以设置过期时间
            return rSet;
        }
        return null;
    }

    /**
     * key是否存于分布式set对象中
     * @param key
     * @return
     */
    public Boolean existKey(final String key){
        Boolean result=false;
        RSet<String> rSet=redisson.getSet(key);
        if (rSet!=null && rSet.size()>0){
            result=true;
        }
        return result;
    }

}






























