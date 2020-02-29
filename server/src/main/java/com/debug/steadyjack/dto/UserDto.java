package com.debug.steadyjack.dto;/**
 * Created by Administrator on 2018/10/25.
 */

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2018/10/25 22:14
 * @Link:QQ-1974544863
 **/
@Data
public class UserDto {

    @NotBlank
    private String userName;

    private String email;

}
































