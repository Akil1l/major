package com.example.demo.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Akil
 * @since 2021-02-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="CardHu对象", description="")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardHu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String cardKey;

    @TableField(exist = false)
    private String cardJiao;

    private Integer cardValue;

    private Integer times;

    @TableField(exist = false)
    private Integer key;

    @TableField(exist = false)
    private Integer value;


}
