package com.bol.mancala.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class BaseBusinessException extends RuntimeException {
    private final String message;
    private HttpStatus httpStatusCode = HttpStatus.BAD_REQUEST;
}
