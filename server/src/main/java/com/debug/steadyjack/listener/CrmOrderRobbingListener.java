package com.debug.steadyjack.listener;/**
 * Created by Administrator on 2018/10/31.
 */

import com.debug.steadyjack.entity.User;
import com.debug.steadyjack.listener.event.CrmOrderRobbingEvent;
import com.debug.steadyjack.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2018/10/31 20:46
 * @Link:QQ-1974544863
 **/
@Component
public class CrmOrderRobbingListener implements ApplicationListener<CrmOrderRobbingEvent>{

    private static final Logger log= LoggerFactory.getLogger(CrmOrderRobbingListener.class);

    @Autowired
    private UserMapper userMapper;


    @Async
    public void onApplicationEvent(CrmOrderRobbingEvent event) {
        log.info("crm系统抢单成功监听到信息：----> {} ",event);

        if (event!=null && event.getUserId()!=null){
            String msg="%s,恭喜您，手机号为：%s 的资源客户已被您抢到，请进行后续的相关跟进操作!!";
            User user=userMapper.selectByPrimaryKey(event.getUserId());
            if (user!=null){
                msg=String.format(msg,user.getUserName(),event.getMobile());

                log.info("crm系统抢单成功监听发送信息--发送到销售人员：----> {} ",msg);
            }
        }
    }
}




































