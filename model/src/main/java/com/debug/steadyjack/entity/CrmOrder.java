package com.debug.steadyjack.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class CrmOrder {
    private Integer id;

    private Integer userId;

    private String mobile;

    private Date createTime;

    private Date updateTime;

    private Integer isActive=1;

}