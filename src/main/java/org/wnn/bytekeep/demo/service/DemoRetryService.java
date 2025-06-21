package org.wnn.bytekeep.demo.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wnn.bytekeep.component.ResilienceExecutor;


/**
 * @author NanNan Wang
 */
@Service
@RequiredArgsConstructor
public class DemoRetryService {

    private final ResilienceExecutor resilienceExecutor;


    // Resilience4j 默认顺序是 CircuitBreaker > Retry；
    @Retry(name = "default")
    @CircuitBreaker(name = "default",fallbackMethod = "fallback")
    public String callRemoteApi(String param) {
        System.out.println("调用远程服务 param=" + param);
        throw new RuntimeException("远程服务失败");
    }

    // 熔断降级方法（参数 + 异常）
    public String fallback(String param, Throwable ex) {
        System.out.println("fallback 执行：" + ex.getMessage());
        return "降级返回：系统繁忙 param=" + param;
    }

    public String callRemoteApi2(String param) {

        return resilienceExecutor.execute(
                "default",
                () -> {
                    System.out.println("编码 调用远程服务：" + param);
                    throw new RuntimeException("远程服务失败");
                },
                throwable -> {
                    System.out.println("编码 fallback 执行：" + throwable.getMessage());
                    return "降级返回：系统繁忙 param=" + param;
                }
        );
    }

}


