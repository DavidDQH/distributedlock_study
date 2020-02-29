package com.debug.steadyjack.service;/**
 * Created by Administrator on 2018/10/25.
 */

import com.debug.steadyjack.dto.UserDto;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2018/10/25 20:10
 * @Link:QQ-1974544863
 **/
public interface IUserService {

    int register(UserDto userDto) throws Exception;

}