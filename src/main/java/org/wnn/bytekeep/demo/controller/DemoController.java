package org.wnn.bytekeep.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.wnn.bytekeep.core.response.ResponseAutoWrap;
import org.wnn.bytekeep.core.response.SkipResponseAutoWrap;
import org.wnn.bytekeep.core.validation.CreateGroup;
import org.wnn.bytekeep.demo.dto.User;
import org.wnn.bytekeep.demo.service.DemoRetryService;

/**
 * @author NanNan Wang
 */
@RestController
@RequestMapping("/demo")
@ResponseAutoWrap
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"测试"},value = "测试接口")
public class DemoController {

    private final DemoRetryService demoRetryService;

    @ApiOperation(value = "retry 测试接口，测试重试和降级。")
    @GetMapping("/retry")
    public void testRetry() {
        demoRetryService.callRemoteApi("123");
//        demoRetryService.callRemoteApi2("123");
    }

    @ApiOperation(value = "GET 测试接口，主要用来测试统一结果封装，String 类型返回。")
    @GetMapping("/hello")
    public String hello(@ApiParam(value = "用户姓名",required = false) @RequestParam(name = "name", defaultValue = "unknown user") String name) {
        log.info("测试日志traceId");
        return "Hello " + name;
    }


    @ApiOperation(value = "GET 测试接口，测试 path var。")
    @GetMapping("/say_hey/{name}")
    @ApiImplicitParam(name = "name", value = "名称", dataType = "String")
    public String sayHey(@PathVariable String name) {
        return "Hey " + name;
    }

    @GetMapping("/user")
    @SkipResponseAutoWrap
    public User user() {
        User user = new User();
        user.setName("theonefx");
        user.setAge(666);
        return user;
    }

    @ApiOperation(value = "POST 测试接口，用来测试参数校验。")
    @PostMapping("/add_user")
    public String addUser(@Validated({CreateGroup.class}) @RequestBody User user) {
        return "add user " + user.getName();
    }


}

