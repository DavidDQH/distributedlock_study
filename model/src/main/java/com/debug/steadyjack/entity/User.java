package com.debug.steadyjack.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class User {
    private Integer id;

    private String userName;

    private String email;

    private Date createTime;

    private Date updateTime;
}