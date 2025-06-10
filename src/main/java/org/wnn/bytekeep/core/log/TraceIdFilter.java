package org.wnn.bytekeep.core.log;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 日志追踪
 *
 * @author NanNan Wang
 */
@Component
@Order(1)
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 类型转换
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. 尝试从请求头获取已有 traceId（用于微服务链路透传）
        String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.trim().isEmpty()) {
            // 2. 没有则生成一个新的
            traceId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        }

        try {
            // 3. 设置到 MDC 供日志使用
            MDC.put(TRACE_ID_KEY, traceId);

            // 4. 设置响应头（核心代码）
            httpResponse.setHeader(TRACE_ID_HEADER, traceId);

            // 5. 继续调用后续过滤器链
            chain.doFilter(request, response);
        } finally {
            // 6. 请求结束时清除 MDC
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
