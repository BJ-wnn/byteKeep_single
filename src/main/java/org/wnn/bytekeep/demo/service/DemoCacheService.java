package org.wnn.bytekeep.demo.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.wnn.bytekeep.constant.CacheNames;
import org.wnn.bytekeep.demo.dto.User;

/**
 * @author NanNan Wang
 */
@Service
public class DemoCacheService {

    @Cacheable(cacheNames = CacheNames.DEMO_USER, key = "'DemoCacheService.finUserInfoByid:' + #id")
    public User getUserById(String id) {
        try {
            Thread.sleep(5000); // 模拟 5 秒延迟
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        User user = new User();
        user.setId(Integer.parseInt(id));
        user.setName("wangnannan");
        return user;
    }


}
