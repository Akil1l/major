package com.example.demo.controller;


import com.example.demo.service.CardHuService;
import com.example.demo.utils.Result;
import io.swagger.annotations.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Akil
 * @since 2021-02-23
 */
@RestController
@RequestMapping("/cardHu")
public class CardHuController {
    private final CardHuService cardHuService;

    public CardHuController(CardHuService cardHuService) {
        this.cardHuService = cardHuService;
    }

    @ApiOperation("生成牌局")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 300, message = "请求失败"),
    })
    @GetMapping("/start")
    public Result start() {
        return Result.createBySuccess(cardHuService.start());
    }

    @ApiOperation("选择")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 300, message = "请求失败"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "0碰,1胡,2杠")
    })
    @GetMapping("/chose")
    public Result chose(Integer card, Integer index, Boolean peng, Boolean hu, Boolean gang) {
        return Result.createBySuccess(cardHuService.chose(card, index, peng, hu, gang));
    }

    @ApiOperation("生成听牌表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 300, message = "请求失败"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "0碰,1胡,2杠")
    })
    @GetMapping("/operate")
    public Result operate(Integer card, Integer index, Integer type) {
        return Result.createBySuccess(cardHuService.operate(card, index, type));
    }

    @ApiOperation("弃牌")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 300, message = "请求失败"),
    })
    @GetMapping("/disCard")
    public Result disCard(Integer card, Integer index, Integer type) {
        return Result.createBySuccess(cardHuService.discard(card, index, type));
    }


    @ApiOperation("生成胡牌表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 300, message = "请求失败"),
    })
    @GetMapping("/generateCardKey")
    public Result generateCardKey() {
        return Result.createBySuccess(cardHuService.generateCardKey());
    }

    @ApiOperation("学习")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 300, message = "请求失败"),
    })
    @GetMapping("/learn")
    public Result learn() {
        return Result.createBySuccess(cardHuService.learn());
    }

    @ApiOperation("暂停")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 300, message = "请求失败"),
    })
    @GetMapping("/pause")
    public Result pause() {
        return Result.createBySuccess(cardHuService.pause());
    }
}
