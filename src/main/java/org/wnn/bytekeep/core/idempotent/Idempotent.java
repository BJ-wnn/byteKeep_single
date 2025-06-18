package org.wnn.bytekeep.core.idempotent;

import java.lang.annotation.*;

/**
 * @author NanNan Wang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    String keyName() default "Idempotency-Key"; // HTTP Header 名
    long expireSeconds() default 300; // 默认有效时间：5分钟
}
