package org.repository_getter.handler;

import org.repository_getter.model.CustomError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class HttpExceptionHandler {

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public final ResponseEntity<?> handleHttpMediaTypeNotAcceptableException(final HttpMediaTypeNotAcceptableException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).contentType(MediaType.APPLICATION_JSON)
                .body(CustomError.builder()
                        .status(406)
                        .message(e.getMessage())
                        .build());
    }
}
