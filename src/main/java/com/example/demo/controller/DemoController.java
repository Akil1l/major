package com.example.demo.controller;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: demo
 * @description: 控制层
 * @author: Akil
 * @create: 2021-02-03 15:31r
 **/
@Api(tags = "demo")
@CrossOrigin
@RestController
@RequestMapping("/index")
public class DemoController {

    @GetMapping("/demo")
    @ApiOperation("Hello World")
    @ApiResponses({
            @ApiResponse(code = 200,message = "请求成功"),
            @ApiResponse(code = 300,message = "请求失败"),
    })
    @ApiImplicitParam(name = "demo", value = "demo", dataType = "String")
    public String getString(String demo){
        return "Hello World"+demo;
    }
}
