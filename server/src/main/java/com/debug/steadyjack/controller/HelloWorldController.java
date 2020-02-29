package com.debug.steadyjack.controller;

import com.debug.steadyjack.entity.ProductLock;
import com.debug.steadyjack.mapper.ProductLockMapper;
import com.debug.steadyjack.response.BaseResponse;
import com.debug.steadyjack.response.StatusCode;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by steadyjack on 2018/10/17.
 */
@RestController
public class HelloWorldController {

    private static final Logger log= LoggerFactory.getLogger(HelloWorldController.class);

    private static final String prefix="hello";

    @Autowired
    private ProductLockMapper productLockMapper;

    /**
     * hello world测试
     * @return
     */
    @RequestMapping(value = prefix+"/world",method = RequestMethod.GET)
    public BaseResponse helloWorld(){
        BaseResponse response=new BaseResponse(StatusCode.Ok);
        try {
            //TODO：我们真正的处理逻辑

            response.setData("hello-world");

        }catch (Exception e){
            log.error("发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail);
        }
        return response;
    }


    @RequestMapping(value = prefix+"/product/detail/{id}",method = RequestMethod.GET)
    public BaseResponse detail(@PathVariable Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Ok);
        if (id==null || id<=0){
            return new BaseResponse(StatusCode.InvalidParam);
        }
        try {
            //TODO：我们真正的处理逻辑
            ProductLock lock=productLockMapper.selectByPrimaryKey(id);
            response.setData(lock);

        }catch (Exception e){
            log.error("发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail);
        }
        return response;
    }



}










































