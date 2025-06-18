package org.wnn.bytekeep.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.wnn.bytekeep.constant.CacheKeysPrefix;
import org.wnn.bytekeep.core.idempotent.IdempotentTokenService;

import static org.wnn.bytekeep.constant.CacheNames.TOKEN;

/**
 * @author NanNan Wang
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CaffeineIdempotentTokenService implements IdempotentTokenService {

    private final CacheHelper cacheHelper;

    @Override
    public boolean tryUseToken(String token) {
        String cacheKey = CacheKeysPrefix.TOKEN_PREFIX + token;
        log.info("幂等key=" + cacheKey);
        // 线程不安全 不是原子操作
//        String status = cacheHelper.get(TOKEN, cacheKey);
//        if (status == null) {
//            throw new IllegalArgumentException("Token 不存在或已过期");
//        }
//
//        if ("USED".equals(status)) {
//            return false; // 幂等拦截
//        }
//
//        // 正常使用，标记为 USED
//        cacheHelper.put(TOKEN, cacheKey, "USED");
//        return true;

        String current = cacheHelper.get(TOKEN, cacheKey);
        if (current == null) {
            throw new IllegalArgumentException("Token 不存在或已过期");
        }

        // 原子地将 "UNUSED" 替换为 "USED"
        boolean updated = cacheHelper.compareAndSet(TOKEN, cacheKey, "UNUSED", "USED");

        if (!updated) {
            log.debug("Token [{}] 已被使用，幂等拦截", token);
        }

        return updated;
    }

}
