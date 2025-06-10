package org.wnn.bytekeep.core.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author NanNan Wang
 */
@ControllerAdvice
public class GlobalResponseWrapperAdvice implements ResponseBodyAdvice<Object> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果标记了 @SkipAutoWrapResponse，直接跳过封装
        if (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), SkipResponseAutoWrap.class) ||
                returnType.hasMethodAnnotation(SkipResponseAutoWrap.class)) {
            return false;
        }

        // 只有标记了 @AutoWrapResponse 的才进行封装
        return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseAutoWrap.class) ||
                returnType.hasMethodAnnotation(ResponseAutoWrap.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        // 如果已经是封装后的格式，直接返回
        if (body instanceof CommonResponse || body instanceof ResponseEntity) {
            CommonResponse<?> apiResponse = (CommonResponse<?>) body;
            MDC.put("bizCode", String.valueOf(apiResponse.getCode()));
            return body;
        }

        // Spring MVC 对 String 类型的返回值会直接处理为 StringHttpMessageConverter，导致你包装成对象会报错。
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(CommonResponse.success(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("String 类型序列化失败", e);
            }
        }
        MDC.put("bizCode", String.valueOf(ResultCode.SUCCESS.getCode()));
        return CommonResponse.success(body);
    }
}

