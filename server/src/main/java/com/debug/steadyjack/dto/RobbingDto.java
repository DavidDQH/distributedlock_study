package com.debug.steadyjack.dto;/**
 * Created by Administrator on 2018/10/31.
 */

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2018/10/31 20:06
 * @Link:QQ-1974544863
 **/
@Data
@ToString
public class RobbingDto {

    @NotNull
    private Integer userId;

    @NotBlank
    private String mobile;

}
























































