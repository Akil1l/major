package com.example.demo.service;

import com.example.demo.entity.WarZone;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Akil
 * @since 2021-04-09
 */
public interface WarZoneService extends IService<WarZone> {

    Object learn(Integer index);

    Integer test(WarZone warZone);
}
