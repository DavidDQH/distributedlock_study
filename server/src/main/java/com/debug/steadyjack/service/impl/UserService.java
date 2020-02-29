package com.debug.steadyjack.service.impl;/**
 * Created by Administrator on 2018/10/25.
 */

import com.debug.steadyjack.components.DistributeRedisLock;
import com.debug.steadyjack.dto.UserDto;
import com.debug.steadyjack.entity.User;
import com.debug.steadyjack.mapper.UserMapper;
import com.debug.steadyjack.service.IUserService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2018/10/25 20:10
 * @Link:QQ-1974544863
 **/
@Service
public class UserService implements IUserService{

    private static final Logger log= LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DistributeRedisLock redisLock;

    private static final String lockKeyPrefix="redisson:userName:";

    @Autowired
    private CuratorFramework client;

    private static final String zkPrefix="/repeat/submit/";

    private static final String zkRedisKeyPrefix="zkRedis:repeat:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;




    /**
     * 注册
     * @param userDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int register(UserDto userDto) throws Exception{
//        int result=0;
//        User user=new User();
//        BeanUtils.copyProperties(userDto,user);
//        user.setCreateTime(new Date());
//        userMapper.insertSelective(user);
//        return result; //v1.0

        //v2.0
//        int result=0;
//
//        //TODO：思路-（1）为共享资源加锁；（2）为共享资源在某个缓存之地 如rset 加入存在的标识-就是将共享资源加入就好了!!
//
//        RLock rLock=redisLock.acquireLock(userDto.getUserName());
//        try {
//            String key=lockKeyPrefix+userDto.getUserName();
//            if (!redisLock.existKey(key)){
//                redisLock.setKeyValue(key,userDto.getUserName());
//
//                User user=new User();
//                BeanUtils.copyProperties(userDto,user);
//                user.setCreateTime(new Date());
//                userMapper.insertSelective(user);
//            }else{
//                log.info("已存在于redisson的set存储中:{}",key);
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//            throw e;
//        }finally {
//            redisLock.realeaseLock(rLock);
//        }
//
//        return result;


        //v3.0
        int result=0;

        InterProcessMutex mutex=new InterProcessMutex(client,zkPrefix+userDto.getUserName()+"-lock");
        try {
            if (mutex.acquire(10L, TimeUnit.SECONDS)){

                final String realKey=zkRedisKeyPrefix+userDto.getUserName();
                if (!stringRedisTemplate.hasKey(realKey)){
                    stringRedisTemplate.opsForValue().set(realKey, UUID.randomUUID().toString());

                    User user=new User();
                    BeanUtils.copyProperties(userDto,user);
                    user.setCreateTime(new Date());
                    userMapper.insertSelective(user);
                }else{
                    log.info("已存在于redis的key中:{}",realKey);
                }

            }else{
                throw new RuntimeException("获取zk分布式锁失败!");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            mutex.release();
        }

        return result;

    }
}


































