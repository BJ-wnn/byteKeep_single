//package org.wnn.bytekeep.component;
//
//import org.wnn.bytekeep.core.idempotent.IdempotentTokenService;
//
///**
// * @author NanNan Wang
// */
//public class RedisIdempotentTokenService implements IdempotentTokenService {
//
//    private final StringRedisTemplate redisTemplate;
//    private final IdempotentProperties properties;
//
//    @Override
//    public boolean tryUseToken(String token) {
//        String key = "idempotent:" + token;
//        Boolean success = redisTemplate.opsForValue()
//                .setIfAbsent(key, "USED", Duration.ofSeconds(properties.getExpire()));
//        return Boolean.TRUE.equals(success);
//    }
//}
