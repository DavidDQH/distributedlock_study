package com.debug.steadyjack.controller;/**
 * Created by Administrator on 2018/10/25.
 */

import com.debug.steadyjack.dto.UserDto;
import com.debug.steadyjack.entity.User;
import com.debug.steadyjack.mapper.UserMapper;
import com.debug.steadyjack.response.BaseResponse;
import com.debug.steadyjack.response.StatusCode;
import com.debug.steadyjack.service.IUserService;
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
 * @Date: 2018/10/25 22:08
 * @Link:QQ-1974544863
 **/
@RestController
public class UserController {

    private static final Logger log= LoggerFactory.getLogger(UserController.class);

    private static final String prefix="user";

    @Autowired
    private IUserService userService;

    @Autowired
    private UserMapper userMapper;


    /**
     * 用户信息注册
     * @param userDto
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix+"/register",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse register(@RequestBody @Validated UserDto userDto, BindingResult bindingResult){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.debug("注册信息： {} ",userDto);

            User user=userMapper.selectByUserName(userDto.getUserName());
            if (user!=null){
                return new BaseResponse(StatusCode.UserNameExist);
            }

            userService.register(userDto);
        }catch (Exception e){
            e.printStackTrace();
            response=new BaseResponse(StatusCode.Fail);
        }
        return response;
    }
}











































