package com.debug.steadyjack.service;

import com.debug.steadyjack.components.DistributeRedisLock;
import com.debug.steadyjack.dto.ProductLockDto;
import com.debug.steadyjack.entity.ProductLock;
import com.debug.steadyjack.mapper.ProductLockMapper;
import com.google.common.base.Strings;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/10/17.
 */
@Service
public class DataLockService {

    private static final Logger log= LoggerFactory.getLogger(DataLockService.class);

    @Autowired
    private ProductLockMapper lockMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private final static String countKey="redis:lock:count";


    @Autowired
    private CuratorFramework client;

    private static final String pathPrefix="/springboot/zkLock/";


    @Autowired
    private DistributeRedisLock distributeRedisLock;




    /**
     * 正常更新商品库存 - 重现了高并发的场景
     * @param dto
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateStock(ProductLockDto dto) throws Exception{
        int res=0;

        ProductLock entity=lockMapper.selectByPrimaryKey(dto.getId());
        if (entity!=null && entity.getStock().compareTo(dto.getStock())>=0){
            entity.setStock(dto.getStock());
            return lockMapper.updateStock(entity);
        }

        return res;
    }





    /**
     * 更新商品库存-乐观锁
     * @param dto
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateStockPositive(ProductLockDto dto) throws Exception{
        int res=0;

        //TODO:每个线程查询出资源的时候需要带出version字段信息
        ProductLock entity=lockMapper.selectByPrimaryKey(dto.getId());

        //TODO：然后再在更新库存的时候同时带上version的判断并进行递增
        if (entity!=null && entity.getStock().compareTo(dto.getStock())>=0){
            entity.setStock(dto.getStock());
            res = lockMapper.updateStockV1(entity);
            if (res>0){
                log.info("更新成功的数据------>: stock={} ",dto.getStock());
            }
            return res;
        }

        return res;
    }


    /**
     * 更新商品库存-悲观锁
     * @param dto
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateStockNegative(ProductLockDto dto) throws Exception{
        int res=0;

        ProductLock entity=lockMapper.selectByPKForNegative(dto.getId());
        if (entity!=null && entity.getStock().compareTo(dto.getStock())>=0){
            entity.setStock(dto.getStock());
            res=lockMapper.updateStockForNegative(entity);

            if (res>0){
                log.info("更新商品库存-悲观锁-成功：stock={} ",dto.getStock());
            }
        }

        return res;
    }


    /**
     * 基于redis实战分布式锁
     * @param dto
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateStockRedis(ProductLockDto dto) throws Exception{
        int result=0;

//        //TODO:只有当当前key不存在的时候，SETNX 会成功 – 此时相当于获取到可以对这个资源进行操作的同步锁
//        final String key=String.format("redis:product:id:%s",dto.getId()+"");
//        String value=UUID.randomUUID().toString()+System.nanoTime();
//
//        Boolean res=stringRedisTemplate.opsForValue().setIfAbsent(key,value);
//        try {
//            if (res){
//                stringRedisTemplate.opsForValue().increment(countKey,1L);
//
//                //TODO：核心逻辑
//                ProductLock entity=lockMapper.selectByPrimaryKey(dto.getId());
//                if (entity!=null && entity.getStock().compareTo(dto.getStock())>=0){
//                    entity.setStock(dto.getStock());
//                    result=lockMapper.updateStockForNegative(entity);
//
//                    if (result>0){
//                        log.info("基于redis实战分布式锁-成功：stock={} ",dto.getStock());
//                    }
//                }
//            }else{
//                //TODO；在这里失败的话就丢弃了!
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            //TODO：不管发生什么情况，我都要释放掉你的锁
//            //TODO：操作完毕 需要释放掉锁 – 要确保释放的锁是当时获取到的锁  （其实可以巧妙地通过value的比较来实现）
//
//            if (value.equals(stringRedisTemplate.opsForValue().get(key))){
//                stringRedisTemplate.delete(key);
//            }
//        }



        //TODO:只有当当前key不存在的时候，SETNX 会成功 – 此时相当于获取到可以对这个资源进行操作的同步锁
        final String key=String.format("redis:product:id:%s",dto.getId()+"");

        Boolean res=true;
        while(res){
            String value= UUID.randomUUID().toString()+System.nanoTime();
            //这个key是否存在
            res=stringRedisTemplate.opsForValue().setIfAbsent(key,value);

            if (res){
                //统计key 的总量  线程进来多少次
                stringRedisTemplate.opsForValue().increment(countKey,1L);

                try {
                    //TODO：执行真正的处理逻辑
                    res=false;

                    ProductLock entity=lockMapper.selectByPrimaryKey(dto.getId());
                    if (entity!=null && entity.getStock().compareTo(dto.getStock())>=0){
                        entity.setStock(dto.getStock());
                        result=lockMapper.updateStockForNegative(entity);

                        if (result>0){
                            log.info("基于redis实战分布式锁-成功：stock={} ",dto.getStock());
                        }else{
                            //TODO：发送一条异步信息，记录客户的手机号，等待下一次搞活动通知!
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    //TODO：释放锁-释放当时自己获取到的锁-value
                    String redisValue=stringRedisTemplate.opsForValue().get(key);
                    if (!Strings.isNullOrEmpty(redisValue) && redisValue.equals(value)){
                        stringRedisTemplate.delete(key);
                    }
                }
            }else{
                res=true;
                Thread.sleep(1000);
            }
        }

        return result;
    }



    /**
     * 基于zookeepr实战分布式锁
     * @param dto
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateStockZookeeper(ProductLockDto dto) throws Exception{
        int res=0;

        InterProcessMutex mutex=new InterProcessMutex(client,pathPrefix+dto.getId()+"-lock");
        try {
            if (mutex.acquire(10L, TimeUnit.SECONDS)){

                //TODO：真正的核心处理逻辑
                ProductLock entity=lockMapper.selectByPrimaryKey(dto.getId());
                if (entity!=null && entity.getStock().compareTo(dto.getStock())>=0){
                    entity.setStock(dto.getStock());
                    res=lockMapper.updateStockForNegative(entity);

                    if (res>0){
                        log.info("基于zookeepr实战分布式锁-成功：stock={} ",dto.getStock());
                    }
                }
            }else{
                throw new RuntimeException("获取zk分布式锁失败!");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            //TODO：处理完毕，要释放锁
            mutex.release();
        }

        return res;
    }



    /**
     * 基于redisson实战分布式锁
     * @param dto
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateStockRedisson(ProductLockDto dto) throws Exception{
        int res=0;

        RLock rLock=distributeRedisLock.acquireLock(String.valueOf(dto.getId()));
        try {
            if (rLock!=null){

                //TODO：真正的核心处理逻辑
                ProductLock entity=lockMapper.selectByPrimaryKey(dto.getId());
                if (entity!=null && entity.getStock().compareTo(dto.getStock())>=0){
                    entity.setStock(dto.getStock());
                    res=lockMapper.updateStockForNegative(entity);

                    if (res>0){
                        log.info("基于redisson实战分布式锁-成功：stock={} ",dto.getStock());
                    }
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

        return res;
    }
}















































