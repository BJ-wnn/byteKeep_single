package org.wnn.bytekeep.component;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author NanNan Wang
 */
@Component
@RequiredArgsConstructor
public class ResilienceExecutor {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    /**
     * 执行带熔断 + 重试 + 降级的操作
     * @param name 配置实例名（用于匹配 application.yml）
     * @param businessLogic 核心业务逻辑 Supplier
     * @param fallback 降级处理逻辑（Throwable -> 默认值）
     * @return 执行结果
     * @param <T> 返回值类型
     */
    public <T> T execute(String name, Supplier<T> businessLogic, Function<Throwable, T> fallback) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);
        Retry retry = retryRegistry.retry(name);

        // 手动组合装饰器（顺序：先重试，再熔断）
        Supplier<T> retrySupplier = Retry.decorateSupplier(retry, businessLogic);
        Supplier<T> circuitBreakerSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, retrySupplier);

        // 使用 Try 处理异常并执行降级逻辑
        return Try.ofSupplier(circuitBreakerSupplier)
                .recover(fallback::apply)
                .get();
    }

    /**
     * 只启用重试机制，不进行熔断和降级。
     * 若最终失败，异常将直接抛出。
     *
     * @param name 配置名称
     * @param businessLogic 核心业务逻辑
     * @return 执行结果
     * @param <T> 返回值类型
     */
    public <T> T  executeWithRetryOnly(String name, Supplier<T> businessLogic) {
        Retry retry = retryRegistry.retry(name);
        Supplier<T> retrySupplier = Retry.decorateSupplier(retry, businessLogic);
        return Try.ofSupplier(retrySupplier)
                .get();
    }


    /**
     * 启用重试机制，并在最终失败时执行降级逻辑。
     *
     * @param name 配置名称
     * @param businessLogic 核心业务逻辑
     * @param fallback 降级处理函数
     * @return 执行结果或降级后的默认值
     * @param <T> 返回值类型
     */
    public <T> T executeWithRetryOnly(String name, Supplier<T> businessLogic, Function<Throwable, T> fallback) {
        Retry retry = retryRegistry.retry(name);
        Supplier<T> retrySupplier = Retry.decorateSupplier(retry, businessLogic);

        return Try.ofSupplier(retrySupplier)
                .recover(fallback::apply)
                .get();
    }

    /**
     * 启用熔断机制，并在熔断触发时执行降级逻辑。
     *
     * @param name 配置名称
     * @param businessLogic 核心业务逻辑
     * @param fallback 降级处理函数
     * @return 执行结果或降级后的默认值
     * @param <T> 返回值类型
     */
    public <T> T executeWithCircuitBreakerOnly(String name, Supplier<T> businessLogic, Function<Throwable, T> fallback) {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(name);
        Supplier<T> cbSupplier = CircuitBreaker.decorateSupplier(cb, businessLogic);

        return Try.ofSupplier(cbSupplier)
                .recover(fallback::apply)
                .get();
    }


    /**
     * 同时启用熔断和重试机制，但不提供降级逻辑。
     * 若最终失败，异常将直接抛出。
     *
     * @param name 配置名称
     * @param businessLogic 核心业务逻辑
     * @return 执行结果
     * @param <T> 返回值类型
     */
    public <T> T executeWithoutFallback(String name, Supplier<T> businessLogic) {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(name);
        Retry retry = retryRegistry.retry(name);

        Supplier<T> retrySupplier = Retry.decorateSupplier(retry, businessLogic);
        Supplier<T> cbSupplier = CircuitBreaker.decorateSupplier(cb, retrySupplier);

        return cbSupplier.get(); // Let exception propagate
    }

    /**
     * 执行无返回值的任务（Runnable），支持熔断、重试和降级。
     * 自动将 Runnable 转换为 Supplier<Void>。
     *
     * @param name 配置名称
     * @param task 任务逻辑
     * @param fallback 降级处理函数
     */
    public void executeVoid(String name, Runnable task, Function<Throwable, Void> fallback) {
        execute(name, () -> {
            task.run();
            return null;
        }, fallback);
    }

    /**
     * 异步执行任务，支持熔断、重试及降级。
     *
     * @param name 配置名称
     * @param businessLogic 核心业务逻辑
     * @param fallback 降级处理函数
     * @return CompletableFuture 包含执行结果或降级后的默认值
     * @param <T> 返回值类型
     */
    public <T> CompletableFuture<T> executeAsync(String name, Supplier<T> businessLogic, Function<Throwable, T> fallback) {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(name);
        Retry retry = retryRegistry.retry(name);

        Supplier<CompletableFuture<T>> futureSupplier = () ->
                CompletableFuture.supplyAsync(
                        Retry.decorateSupplier(retry,
                                CircuitBreaker.decorateSupplier(cb, businessLogic)
                        )
                ).exceptionally(fallback::apply);

        return futureSupplier.get();
    }
}
