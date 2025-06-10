package org.wnn.bytekeep.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.wnn.bytekeep.core.response.ResponseAutoWrap;
import org.wnn.bytekeep.core.response.SkipResponseAutoWrap;
import org.wnn.bytekeep.core.validation.CreateGroup;
import org.wnn.bytekeep.demos.web.User;

/**
 * @author NanNan Wang
 */
@RestController
@RequestMapping("/demo")
@ResponseAutoWrap
@Slf4j
public class DemoController {

    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
        log.info("测试日志traceId");
        return "Hello " + name;
    }

    @GetMapping("/user")
    @SkipResponseAutoWrap
    public User user() {
        User user = new User();
        user.setName("theonefx");
        user.setAge(666);
        return user;
    }

    @PostMapping("/add_user")
    public String addUser(@Validated({CreateGroup.class}) @RequestBody User user) {
        return "add user " + user.getName();
    }


}

