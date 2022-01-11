package com.example.demo.service;

import com.example.demo.entity.CardHu;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Akil
 * @since 2021-02-23
 */
public interface CardHuService extends IService<CardHu> {

    String generateCardKey();

    String start();

    Boolean discard(Integer card, Integer index,Integer type);

    String operate(Integer card, Integer index, Integer type);

    String chose(Integer card, Integer index, Boolean peng, Boolean hu, Boolean gang);

    String learn();

    Object pause();
}
