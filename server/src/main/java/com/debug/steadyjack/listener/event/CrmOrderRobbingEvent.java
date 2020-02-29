package com.debug.steadyjack.listener.event;/**
 * Created by Administrator on 2018/10/31.
 */

import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2018/10/31 20:47
 * @Link:QQ-1974544863
 **/
public class CrmOrderRobbingEvent extends ApplicationEvent implements Serializable{

    private String mobile;

    private Integer userId;

    public CrmOrderRobbingEvent(Object source, String mobile, Integer userId) {
        super(source);
        this.mobile = mobile;
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CrmOrderRobbingEvent{" +
                "mobile='" + mobile + '\'' +
                ", userId=" + userId +
                '}';
    }
}




































