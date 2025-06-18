package org.wnn.bytekeep.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.wnn.bytekeep.core.idempotent.Idempotent;
import org.wnn.bytekeep.core.response.ResponseAutoWrap;
import org.wnn.bytekeep.demo.dto.User;
import org.wnn.bytekeep.demo.service.DemoCacheService;

/**
 * @author NanNan Wang
 */
@RestController
@RequestMapping("/demo/cache")
@ResponseAutoWrap
@Slf4j
@Api(tags = {"缓存"},value = "测试缓存接口")
@RequiredArgsConstructor
public class DemoCacheController {

    private final DemoCacheService cacheService;

    @ApiOperation(value = "GET 测试缓存接口，主要用来测试缓存功能。")
    @ApiImplicitParam(name = "id", value = "用户id", dataType = "String")
    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return cacheService.getUserById(id);
    }


    @Idempotent
    @PostMapping("/idempotent")
    public String testIdempotent() {
        return "幂等第一次成功！";
    }
}
