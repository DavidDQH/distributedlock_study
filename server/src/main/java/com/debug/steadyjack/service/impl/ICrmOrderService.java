package com.debug.steadyjack.service.impl;


import com.debug.steadyjack.dto.RobbingDto;

/**
 * crm抢单服务
 * Created by Administrator on 2018/10/31.
 */
public interface ICrmOrderService {

    void robbing(RobbingDto dto) throws Exception;

    void robbingV2(RobbingDto dto) throws Exception;

    void robbingV3(RobbingDto dto) throws Exception;

    void robbingV4(RobbingDto dto) throws Exception;
}
