package com.debug.steadyjack.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Created by steadyjack on 2018/9/26.
 */
@Data
@ToString
public class ProductLockDto {

    @NotNull
    private Integer id;

    @NotNull
    private Integer stock;
}
