package com.debug.steadyjack.service;/**
 * Created by Administrator on 2018/10/31.
 */

import com.debug.steadyjack.components.DistributeRedisLock;
import com.debug.steadyjack.dto.RobbingDto;
import com.debug.steadyjack.entity.CrmOrder;
import com.debug.steadyjack.entity.ProductLock;
import com.debug.steadyjack.listener.event.CrmOrderRobbingEvent;
import com.debug.steadyjack.mapper.CrmOrderMapper;
import com.debug.steadyjack.service.impl.ICrmOrderService;
import com.google.common.base.Strings;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2018/10/31 20:08
 * @Link:QQ-1974544863
 **/
@Service
public class CrmOrderService implements ICrmOrderService {

    private static final Logger log= LoggerFactory.getLogger(CrmOrderService.class);

    @Autowired
    private CrmOrderMapper crmOrderMapper;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private CuratorFramework client;

    private static final String lockPath="/crm/order/";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String onlyRedisKey="redis:only:key";


    private static String crmOrderRedisKey="redis:crm:order:key:%s";

    @Autowired
    private DistributeRedisLock distributeRedisLock;


    /**
     * 抢单处理逻辑
     * @param dto
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void robbing(RobbingDto dto) throws Exception {
        Boolean result=this.mobileHasRobbedDataBase(dto.getMobile());
        if (result){
            //TODO:该手机号已经被抢
            log.info("该手机号已经被抢：{} --结束逻辑",dto.getMobile());

        }else{
            //TODO:该手机号还没有被抢
            log.info("该手机号还没有被抢：{} ",dto.getMobile());


            //TODO:手机号记录入抢单表
            CrmOrder entity=new CrmOrder();
            BeanUtils.copyProperties(dto,entity);
            crmOrderMapper.insertSelective(entity);

            //TODO:异步发送抢单成功信息到销售人员
            CrmOrderRobbingEvent event=new CrmOrderRobbingEvent(this,dto.getMobile(),dto.getUserId());
            publisher.publishEvent(event);
        }
    }


    /**
     * 判断当前手机号是否已被抢到-数据库方式
     * @param mobile
     */
    private Boolean mobileHasRobbedDataBase(final String mobile){
        int total=crmOrderMapper.countByMobile(mobile);
        if (total>0){
            return true;
        }
        return false;
    }

    /**
     * 抢单处理逻辑-v2
     * @param dto
     * @throws Exception
     */
    public void robbingV2(RobbingDto dto) throws Exception {
        //TODO:加分布式锁-解决同一时间内只能有一个线程操作共享资源
        InterProcessMutex mutex=new InterProcessMutex(client,lockPath+dto.getMobile()+"-lock");
        try {
            if (mutex.acquire(10L, TimeUnit.SECONDS)){
                Boolean result=this.mobileHasRobbedRedis(dto.getMobile());
                if (result){

                }else{
//                    //TODO:再加一层redis原子操作-分布式锁
//                    String value= UUID.randomUUID().toString()+System.nanoTime();
//                    Boolean res=stringRedisTemplate.opsForValue().setIfAbsent(onlyRedisKey,value);
//
//                    if (res){
//                        try {
//                            //TODO：执行真正的处理逻辑
//
//                            crmOrderRedisKey = String.format(crmOrderRedisKey,dto.getMobile());
//                            stringRedisTemplate.opsForValue().set(crmOrderRedisKey,dto.getMobile(),31L,TimeUnit.DAYS);
//
//                            CrmOrder entity=new CrmOrder();
//                            BeanUtils.copyProperties(dto,entity);
//                            crmOrderMapper.insertSelective(entity);
//
//                            CrmOrderRobbingEvent event=new CrmOrderRobbingEvent(this,dto.getMobile(),dto.getUserId());
//                            publisher.publishEvent(event);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }finally {
//                            //TODO：释放锁
//                            String redisValue=stringRedisTemplate.opsForValue().get(onlyRedisKey);
//                            if (!Strings.isNullOrEmpty(redisValue) && redisValue.equals(value)){
//                                stringRedisTemplate.delete(onlyRedisKey);
//                            }
//                        }
//                    }

                    //TODO：执行真正的处理逻辑

                    crmOrderRedisKey = String.format(crmOrderRedisKey,dto.getMobile());
                    //stringRedisTemplate.opsForValue().set(crmOrderRedisKey,dto.getMobile(),31L,TimeUnit.DAYS);
                    Boolean tempRes=stringRedisTemplate.opsForValue().setIfAbsent(crmOrderRedisKey,dto.getMobile());
                    if (tempRes){
                        CrmOrder entity=new CrmOrder();
                        BeanUtils.copyProperties(dto,entity);
                        crmOrderMapper.insertSelective(entity);

                        CrmOrderRobbingEvent event=new CrmOrderRobbingEvent(this,dto.getMobile(),dto.getUserId());
                        publisher.publishEvent(event);
                    }

                }

            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            mutex.release();
        }
    }

    /**
     * 判断当前手机号是否已被抢到-缓存方式
     * @param mobile
     */
    private Boolean mobileHasRobbedRedis(final String mobile){
        crmOrderRedisKey = String.format(crmOrderRedisKey,mobile);
        if (stringRedisTemplate.hasKey(crmOrderRedisKey)){
            return true;
        }
        return false;
    }

    /**
     * 抢单处理逻辑-v3
     * @param dto
     * @throws Exception
     */
    public void robbingV3(RobbingDto dto) throws Exception {
        //TODO:基于redis原子操作-分布式锁
        String value= UUID.randomUUID().toString()+System.nanoTime();
        Boolean res=stringRedisTemplate.opsForValue().setIfAbsent(onlyRedisKey,value);
        if (res){
            try {
                //TODO：执行真正的处理逻辑-v3
                log.debug("执行真正的处理逻辑-v3");

                crmOrderRedisKey = String.format(crmOrderRedisKey,dto.getMobile());
                //stringRedisTemplate.opsForValue().set(crmOrderRedisKey,dto.getMobile(),31L,TimeUnit.DAYS);
                Boolean tempRes=stringRedisTemplate.opsForValue().setIfAbsent(crmOrderRedisKey,dto.getMobile());
                if (tempRes){
                    CrmOrder entity=new CrmOrder();
                    BeanUtils.copyProperties(dto,entity);
                    crmOrderMapper.insertSelective(entity);

                    CrmOrderRobbingEvent event=new CrmOrderRobbingEvent(this,dto.getMobile(),dto.getUserId());
                    publisher.publishEvent(event);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //TODO：释放锁
                String redisValue=stringRedisTemplate.opsForValue().get(onlyRedisKey);
                if (!Strings.isNullOrEmpty(redisValue) && redisValue.equals(value)){
                    stringRedisTemplate.delete(onlyRedisKey);
                }
            }
        }
    }


    /**
     * 抢单处理逻辑-v4
     * @param dto
     * @throws Exception
     */
    public void robbingV4(RobbingDto dto) throws Exception {
        RLock rLock=distributeRedisLock.acquireLock(String.valueOf(dto.getMobile()));
        try {
            if (rLock!=null){

                String key="redisson:crm:order:"+dto.getMobile();
                if (distributeRedisLock.existKey(key)){
                    //TODO:已经存在-代表已被抢

                }else{
                    //TODO:不存在
                    distributeRedisLock.setKeyValue(key,dto.getMobile());

                    CrmOrder entity=new CrmOrder();
                    BeanUtils.copyProperties(dto,entity);
                    crmOrderMapper.insertSelective(entity);

                    CrmOrderRobbingEvent event=new CrmOrderRobbingEvent(this,dto.getMobile(),dto.getUserId());
                    publisher.publishEvent(event);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            if (rLock!=null){
                distributeRedisLock.realeaseLock(rLock);
            }
        }
    }
}





































