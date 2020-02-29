package com.debug.steadyjack.controller;/**
 * Created by Administrator on 2018/10/31.
 */

import com.debug.steadyjack.dto.RobbingDto;
import com.debug.steadyjack.entity.User;
import com.debug.steadyjack.mapper.UserMapper;
import com.debug.steadyjack.response.BaseResponse;
import com.debug.steadyjack.response.StatusCode;
import com.debug.steadyjack.service.impl.ICrmOrderService;
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
 * @Author:debug (SteadyJack)
 * @Date: 2018/10/31 20:02
 * @Link:QQ-1974544863
 **/
@RestController
public class CrmOrderController {

    private static final Logger log= LoggerFactory.getLogger(CrmOrderController.class);

    private static final String prefix="crm/order";

    @Autowired
    private ICrmOrderService crmOrderService;

    @Autowired
    private UserMapper userMapper;


    /**
     * 抢单请求
     * @param dto
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix+"/robbing",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse robbing(@RequestBody @Validated RobbingDto dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new BaseResponse(StatusCode.InvalidParam);
        }
        User user=userMapper.selectByPrimaryKey(dto.getUserId());
        if (user==null){
            return new BaseResponse(StatusCode.UserNotExist);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            //crmOrderService.robbing(dto);

            //crmOrderService.robbingV2(dto);

            //crmOrderService.robbingV3(dto);

            crmOrderService.robbingV4(dto);
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail);
        }
        return response;
    }


}














































