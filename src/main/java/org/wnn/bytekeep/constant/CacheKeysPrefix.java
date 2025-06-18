package org.wnn.bytekeep.constant;

/**
 * @author NanNan Wang
 */
public class CacheKeysPrefix {
    // 接口幂等api使用，查询 token 是否存在的key前缀，key是前端传入的hash值
    public static final String TOKEN_HASH_PREFIX = "idempotent:token:hash";
    // 缓存用户信息
    public static final String TOKEN_PREFIX = "idempotent:token:";
}
