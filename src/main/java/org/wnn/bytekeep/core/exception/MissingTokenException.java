package org.wnn.bytekeep.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author NanNan Wang
 */
public class MissingTokenException extends ResponseStatusException {

    public MissingTokenException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
