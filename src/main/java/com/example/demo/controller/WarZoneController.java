package com.example.demo.controller;


import com.example.demo.entity.WarZone;
import com.example.demo.service.WarZoneService;
import com.example.demo.utils.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Akil
 * @since 2021-04-09
 */
@RestController
@RequestMapping("/war-zone")
public class WarZoneController {
    private final WarZoneService warZoneService;

    public WarZoneController(WarZoneService warZoneService) {
        this.warZoneService = warZoneService;
    }

    @ApiOperation("学习")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 300, message = "请求失败"),
    })
    @GetMapping("/learn/{index}")
    public Result learn(@PathVariable("index") Integer index) {
        return Result.createBySuccess(warZoneService.learn(index));
    }
    @ApiOperation("测试")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 300, message = "请求失败"),
    })
    @GetMapping("/test")
    public Result test(WarZone warZone) {
        return Result.createBySuccess(warZoneService.test(warZone));
    }
}
