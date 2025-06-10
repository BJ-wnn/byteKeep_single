package org.wnn.bytekeep.core.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author NanNan Wang
 */
@Order(2)  // 必须在 TraceIdFilter 之后
@Component
@EnableConfigurationProperties(RequestLoggingFilter.LoggingProperties.class)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger("reqLog");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObjectMapper objectMapper;
    private final LoggingProperties props;

    public RequestLoggingFilter(LoggingProperties props) {
        this.props = props;
        this.objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        if (shouldSkipLogging(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletRequest wrappedRequest = wrapRequest(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - start;
            logRequest(wrappedRequest, wrappedResponse, duration);
            // 确保无论是否异常都写回响应
            wrappedResponse.copyBodyToResponse();
        }
    }

    private HttpServletRequest wrapRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String contentType = request.getContentType();
        if ((HttpMethod.POST.matches(method) || HttpMethod.PUT.matches(method) || HttpMethod.PATCH.matches(method)) &&
                MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)) {
            return new ContentCachingRequestWrapper(request);
        }
        return request;
    }

    private void logRequest(HttpServletRequest request, ContentCachingResponseWrapper response, long duration) {
        LogEntry log = new LogEntry();

        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        log.setTraceId(traceId);
        log.setTimestamp(LocalDateTime.now().format(FORMATTER));
        log.setMethod(request.getMethod());
        log.setUri(request.getRequestURI());
        log.setQueryString(request.getQueryString());
        log.setRemoteAddr(request.getRemoteAddr());
        log.setDuration(duration);
        // 记录统一封装的结果编码
        String bizCode = MDC.get("bizCode");
        if (bizCode != null) {
            try {
                log.setResponseCode(Integer.parseInt(bizCode));
            } catch (NumberFormatException e) {
                log.setResponseCode(response.getStatus());
            }
        } else {
            log.setResponseCode(response.getStatus());
        }

        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] content = wrapper.getContentAsByteArray();
            try {
                String payload = new String(content, wrapper.getCharacterEncoding());
                log.setPayload(maskSensitiveData(payload));
            } catch (UnsupportedEncodingException e) {
                log.setPayload("[unreadable payload]");
            }
        } else {
            String args = request.getParameterMap().entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                    .collect(Collectors.joining("&"));
            log.setArgs(args);
        }

        if (props.isEnableResponseBody()) {
            byte[] body = response.getContentAsByteArray();
            String responseBody = new String(body, StandardCharsets.UTF_8);
            if (responseBody.length() > props.getMaxResponseLength()) {
                responseBody = responseBody.substring(0, props.getMaxResponseLength()) + "...(truncated)";
            }
            log.setResponseBody(maskSensitiveData(responseBody));
        }

        try {
            logger.info(objectMapper.writeValueAsString(log));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize request log", e);
        } finally {
            MDC.remove("bizCode"); // 防止内存泄漏，清理业务结果编码
        }
    }



    private String maskSensitiveData(String text) {
        return text
                .replaceAll("\"password\"\\s*:\\s*\".*?\"", "\"password\":\"****\"")
                .replaceAll("\"token\"\\s*:\\s*\".*?\"", "\"token\":\"****\"");
    }

    private boolean shouldSkipLogging(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contentType = request.getContentType();
        return uri.contains("/upload") || uri.contains("/download") ||
                (contentType != null && contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) ||
                (contentType != null && contentType.equals(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }

    @Data
    public static class LogEntry {
        private String timestamp;
        private String traceId;
        private String method;
        private String uri;
        private String queryString;
        private String remoteAddr;
        private String args;
        private String payload;
        private int responseCode;
        private String responseBody;
        private long duration;
    }

    @ConfigurationProperties(prefix = "logging.request")
    public static class LoggingProperties {
        private boolean enableResponseBody = false;
        private int maxResponseLength = 2000;

        public boolean isEnableResponseBody() {
            return enableResponseBody;
        }

        public void setEnableResponseBody(boolean enableResponseBody) {
            this.enableResponseBody = enableResponseBody;
        }

        public int getMaxResponseLength() {
            return maxResponseLength;
        }

        public void setMaxResponseLength(int maxResponseLength) {
            this.maxResponseLength = maxResponseLength;
        }
    }
}
