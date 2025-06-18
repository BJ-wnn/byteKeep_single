package org.wnn.bytekeep.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wnn.bytekeep.core.response.ResponseAutoWrap;
import org.wnn.bytekeep.demo.service.DemoInsertService;

/**
 * @author NanNan Wang
 */
@RestController
@RequestMapping("/demo/trans")
@ResponseAutoWrap
@Slf4j
@Api(tags = {"分布式事务"},value = "测试分布式事务")
@RequiredArgsConstructor
public class DemoTransactionController {

    private final DemoInsertService demoInsertService;

    @GetMapping("/insert")
    @ApiOperation(value = "分布式事务")
    public void testInsert() {
        demoInsertService.insertUserAndEmployee();
    }
}
