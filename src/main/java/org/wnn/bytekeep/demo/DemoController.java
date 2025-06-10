package org.wnn.bytekeep.demo;

import org.springframework.web.bind.annotation.*;
import org.wnn.bytekeep.core.response.ResponseAutoWrap;
import org.wnn.bytekeep.demos.web.User;

/**
 * @author NanNan Wang
 */
@RestController
@RequestMapping("/demo")
@ResponseAutoWrap
public class DemoController {

    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
        return "Hello " + name;
    }
    @GetMapping("/user")
    public User user() {
        User user = new User();
        user.setName("theonefx");
        user.setAge(666);
        return user;
    }
}

