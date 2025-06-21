package org.wnn.bytekeep.core.idempotent;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.wnn.bytekeep.core.exception.IdempotentException;
import org.wnn.bytekeep.core.exception.MissingIdempotentTokenException;

import javax.servlet.http.HttpServletRequest;

/**
 * 接口幂等切面，拦截有 @Idempotent 注解的方法
 * 只有应用实现 IdempotentTokenService 接口才会生效。
 * @author NanNan Wang
 */
@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnBean(IdempotentTokenService.class)
public class IdempotentAspect {

    private final HttpServletRequest request;
    private final IdempotentTokenService idempotentTokenService;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String headerName = idempotent.keyName();
        String token = request.getHeader(headerName);

        if (token == null || token.trim().isEmpty()) {
            throw new MissingIdempotentTokenException(HttpStatus.BAD_REQUEST, "缺少幂等性Token");
        }

        if (!idempotentTokenService.tryUseToken(token)) {
            throw new IdempotentException(HttpStatus.CONFLICT, "重复提交");
        }

        return joinPoint.proceed();
    }

}
