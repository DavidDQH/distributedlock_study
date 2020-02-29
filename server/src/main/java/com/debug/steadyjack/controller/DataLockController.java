package com.debug.steadyjack.controller;

import com.debug.steadyjack.dto.ProductLockDto;
import com.debug.steadyjack.response.BaseResponse;
import com.debug.steadyjack.response.StatusCode;
import com.debug.steadyjack.service.DataLockService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by Administrator on 2018/10/17.
 */
@RestController
public class DataLockController {

    private static final Logger log= LoggerFactory.getLogger(DataLockController.class);

    private static final String prefix="lock";

    @Autowired
    private DataLockService dataLockService;

    /**
     * 更新商品库存-1
     * @param dto
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix+"/data/base/positive/update",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse dataBasePositive(@RequestBody @Validated ProductLockDto dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new BaseResponse(StatusCode.InvalidParam);
        }

        BaseResponse response=new BaseResponse(StatusCode.Ok);
        try {
            log.debug("当前请求数据：{} ",dto);

            int res=dataLockService.updateStock(dto);
            if (res<=0) {
                return new BaseResponse(StatusCode.Fail);
            }
        }catch (Exception e){
            log.error("发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail);
        }
        return response;
    }


    /**
     * 更新商品库存-2   mysql乐观锁
     * @param dto
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix+"/data/base/positive/update/v2",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse dataBasePositiveV2(@RequestBody @Validated ProductLockDto dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new BaseResponse(StatusCode.InvalidParam);
        }

        BaseResponse response=new BaseResponse(StatusCode.Ok);
        try {
            log.debug("当前请求数据：{} ",dto);

            int res=dataLockService.updateStockPositive(dto);
            if (res<=0) {
                return new BaseResponse(StatusCode.Fail);
            }
        }catch (Exception e){
            log.error("更新商品库存-2-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail);
        }
        return response;
    }



    /**
     * 更新商品库存-3  mysql悲观锁
     * @param dto
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix+"/data/base/positive/update/v3",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse dataBaseNegative(@RequestBody @Validated ProductLockDto dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new BaseResponse(StatusCode.InvalidParam);
        }

        BaseResponse response=new BaseResponse(StatusCode.Ok);
        try {
            log.debug("当前请求数据：{} ",dto);

            int res=dataLockService.updateStockNegative(dto);
            if (res<=0) {
                return new BaseResponse(StatusCode.Fail);
            }
        }catch (Exception e){
            log.error("更新商品库存-3-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail);
        }
        return response;
    }



    /**
     * 更新商品库存-4   redis实现锁
     * @param dto
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix+"/data/base/positive/update/v4",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse dataBaseRedis(@RequestBody @Validated ProductLockDto dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new BaseResponse(StatusCode.InvalidParam);
        }

        BaseResponse response=new BaseResponse(StatusCode.Ok);
        try {
            log.debug("当前请求数据：{} ",dto);

            int res=dataLockService.updateStockRedis(dto);
            if (res<=0) {
                return new BaseResponse(StatusCode.Fail);
            }
        }catch (Exception e){
            log.error("更新商品库存-4-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail);
        }
        return response;
    }


    /**
     * 更新商品库存-5   zookeeper实现
     * @param dto
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix+"/data/base/positive/update/v5",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse dataBaseZookeeper(@RequestBody @Validated ProductLockDto dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new BaseResponse(StatusCode.InvalidParam);
        }

        BaseResponse response=new BaseResponse(StatusCode.Ok);
        try {
            log.debug("当前请求数据：{} ",dto);

            int res=dataLockService.updateStockZookeeper(dto);
            if (res<=0) {
                return new BaseResponse(StatusCode.Fail);
            }
        }catch (Exception e){
            log.error("更新商品库存-5-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail);
        }
        return response;
    }


    /**
     * 更新商品库存-6  redisson
     * @param dto
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix+"/data/base/positive/update/v6",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse dataBaseRedisson(@RequestBody @Validated ProductLockDto dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new BaseResponse(StatusCode.InvalidParam);
        }

        BaseResponse response=new BaseResponse(StatusCode.Ok);
        try {
            log.debug("当前请求数据：{} ",dto);

            int res=dataLockService.updateStockRedisson(dto);
            if (res<=0) {
                return new BaseResponse(StatusCode.Fail);
            }
        }catch (Exception e){
            log.error("更新商品库存-6-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail);
        }
        return response;
    }

}

















































