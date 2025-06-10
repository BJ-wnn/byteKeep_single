package org.wnn.bytekeep.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.wnn.bytekeep.core.response.CommonResponse;
import org.wnn.bytekeep.core.response.ResultCode;

/**
 * 全局异常处理器，用于统一处理应用程序中的异常
 *
 * @author NanNan Wang
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有未明确指定的异常
     *
     * @param ex 发生的异常对象
     * @return 返回统一格式的错误响应
     */
    @ExceptionHandler(Exception.class)
    public CommonResponse<String> handleException(Exception ex) {
        log.error("系统异常: ", ex);
        return CommonResponse.fail(ResultCode.FAIL);
    }


    /**
     * 处理参数校验失败时抛出的异常（如使用 @Valid 注解校验失败）
     *
     * @param ex 参数校验异常对象
     * @return 返回统一格式的参数错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("参数校验异常: ", ex);
        return CommonResponse.fail(ResultCode.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public CommonResponse<String> handleBindException(BindException ex) {
        log.error("参数校验异常: ", ex);
        return CommonResponse.fail(ResultCode.BAD_REQUEST);
    }

}
