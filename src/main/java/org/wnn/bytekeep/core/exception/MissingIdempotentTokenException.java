package org.wnn.bytekeep.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author NanNan Wang
 */
public class MissingIdempotentTokenException extends ResponseStatusException {

    public MissingIdempotentTokenException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
