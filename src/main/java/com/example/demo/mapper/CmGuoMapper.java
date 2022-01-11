package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.CardHu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Akil
 * @since 2021-02-23
 */
public interface CmGuoMapper extends BaseMapper<CardHu> {

    void generateCardKey(List<CardHu> list);

    String getJiao(String key);

    void ting(List<CardHu> list);

    void insertDateSet(@Param("map") Map<String, Integer> map);

    Map<String, Integer> getDataSet();

    int getCount();
}
