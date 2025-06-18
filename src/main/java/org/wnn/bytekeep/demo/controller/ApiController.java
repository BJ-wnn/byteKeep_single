package org.wnn.bytekeep.demo.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wnn.bytekeep.component.CacheHelper;
import org.wnn.bytekeep.constant.CacheKeysPrefix;
import org.wnn.bytekeep.core.response.ResponseAutoWrap;

import java.util.Map;
import java.util.UUID;

import static org.wnn.bytekeep.constant.CacheNames.TOKEN;

/**
 * @author NanNan Wang
 */
@RestController
@RequestMapping("/api")
@ResponseAutoWrap
@Slf4j
@Api(tags = {"公共服务"},value = "系统公共服务")
@RequiredArgsConstructor
public class ApiController {

    private final CacheHelper cacheHelper;

    private static final String HASH_PREFIX = "idempotent:hash:";
    @PostMapping("/idempotent-token")
    public String generateToken(@RequestBody Map<String, String> param) {
        String hashKey = param.get("hashKey");

        if (hashKey == null || hashKey.trim().isEmpty()) {
            throw new IllegalArgumentException("缺少 hashKey 参数");
        }
        String cacheKey = CacheKeysPrefix.TOKEN_HASH_PREFIX + hashKey;
        // 获取当前 hashKey 对应的 token
        String existingToken = cacheHelper.get(TOKEN, cacheKey);
        if (existingToken != null && !existingToken.trim().isEmpty()) {
            log.info("已存在token,token={}",existingToken);
            return existingToken;
        }

        // 生成新 token
        String newToken = UUID.randomUUID().toString();

        // 同时保存 hash -> token 映射 和 token 状态
        cacheHelper.put(TOKEN, cacheKey, newToken);              // hash -> token
        cacheHelper.put(TOKEN, CacheKeysPrefix.TOKEN_PREFIX + newToken, "UNUSED"); // token 状态
        log.info("放到缓存的的key={},value={}",CacheKeysPrefix.TOKEN_PREFIX + newToken,"UNUSED");
        return newToken;
    }
}
