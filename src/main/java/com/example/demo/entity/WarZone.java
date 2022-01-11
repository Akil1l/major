package com.example.demo.entity;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author Akil
 * @since 2021-04-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "WarZone对象", description = "")
public class WarZone implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer day;

    private Double ashHill;

    private Double thunderPlains;

    private Double hellPortal;

    @ApiModelProperty(hidden = true)
    private Integer exception;

    private Integer type;


}
