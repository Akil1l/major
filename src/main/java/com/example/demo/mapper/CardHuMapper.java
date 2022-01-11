package com.example.demo.mapper;

import com.example.demo.entity.CardHu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Akil
 * @since 2021-02-23
 */
public interface CardHuMapper extends BaseMapper<CardHu> {

    void generateCardKey(List<CardHu> list);
}
