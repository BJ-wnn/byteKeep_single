package org.wnn.bytekeep.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * 接口幂等，重复提交异常
 * @author NanNan Wang
 */
public class IdempotentException  extends ResponseStatusException {

    public IdempotentException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
