package com.udfilasystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RecursoExisteException extends RuntimeException {
    public RecursoExisteException(String message) {
        super(message);
    }
}
