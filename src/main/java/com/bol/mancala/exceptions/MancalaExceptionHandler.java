package com.bol.mancala.exceptions;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.nio.file.AccessDeniedException;

import static org.springframework.http.HttpStatus.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class MancalaExceptionHandler {

    private static final String ERROR_METHOD_ARGUMENT_NOT_VALID = "ERROR_METHOD_ARGUMENT_NOT_VALID";
    private static final String ERROR_METHOD_ARGUMENT_MISS_MACH = "ERROR_METHOD_ARGUMENT_MISS_MACH";
    private static final String ERROR_CONSTRAINT_VIOLATION = "ERROR_CONSTRAINT_VIOLATION";
    private static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
    private static final String ERROR_HTTP_MESSAGE = "ERROR_HTTP_MESSAGE";
    private static final String ERROR_INTERNAL_SERVER = "ERROR_INTERNAL_SERVER";

    private final MessageSource messageSource;


    @ExceptionHandler(BaseBusinessException.class)
    protected ResponseEntity<ApiError> handleBaseBusinessException(
            BaseBusinessException ex) {
        ApiError apiError =
            ApiError.builder()
                    .status(ex.getHttpStatusCode())
                    .code(ERROR_CONSTRAINT_VIOLATION)
                    .message(ex.getMessage())
                    .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ApiError> handleBadCredentialsException(
            AuthenticationException ex) {
        ApiError apiError =
                ApiError.builder()
                        .status(UNAUTHORIZED)
                        .code(AUTHENTICATION_FAILED)
                        .message(ex.getMessage())
                        .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiError> handleAccessDeniedException(
            AccessDeniedException ex) {
        ApiError apiError =
                ApiError.builder()
                        .status(UNAUTHORIZED)
                        .code(AUTHENTICATION_FAILED)
                        .message(ex.getMessage())
                        .build();

        return buildResponseEntity(apiError);
    }


    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        ApiError apiError = ApiError.builder()
                .code(ERROR_CONSTRAINT_VIOLATION)
                .message(ERROR_METHOD_ARGUMENT_NOT_VALID)
                .build();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            apiError.addValidationError(
                    fieldError.getField(),
                    fieldError.getObjectName(),
                    ERROR_METHOD_ARGUMENT_NOT_VALID,
                    fieldError.getDefaultMessage());
        }

        return buildResponseEntity(apiError);
    }


    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        ApiError apiError = ApiError.builder()
                .status(BAD_REQUEST)
                .code(ERROR_CONSTRAINT_VIOLATION)
                .message(ERROR_CONSTRAINT_VIOLATION)
                .build();

        for (ConstraintViolation constraintViolation : ex.getConstraintViolations()) {
            apiError.addValidationError(ERROR_CONSTRAINT_VIOLATION,
                    constraintViolation.getMessage());
        }

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(value = Throwable.class)
    protected ResponseEntity<ApiError> handleInternalServerExceptions(Throwable ex) {

        if (ex instanceof ValidationException && ex.getCause() instanceof ConstraintViolationException) {
            return this.handleConstraintViolation((ConstraintViolationException) ex.getCause());
        }

        if (ex instanceof BaseBusinessException) {
            return this.handleBaseBusinessException((BaseBusinessException) ex);
        }

        ApiError apiError = ApiError.builder()
                .status(INTERNAL_SERVER_ERROR)
                .code(ERROR_INTERNAL_SERVER)
                .message(ex.getMessage())
                .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiError> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        String error = "Malformed JSON request";
        return buildResponseEntity(
                ApiError.builder().status(BAD_REQUEST)
                .code(ERROR_HTTP_MESSAGE)
                .message(error).build());
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    protected ResponseEntity<ApiError> handleHttpMessageNotWritable(
            HttpMessageNotWritableException ex) {
        String error = "Error writing JSON output";

        return buildResponseEntity(
                ApiError.builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .code(ERROR_HTTP_MESSAGE)
                        .message(error)
                        .build());
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        ApiError apiError = ApiError.builder()
                .status(BAD_REQUEST)
                .code(ERROR_METHOD_ARGUMENT_MISS_MACH)
                .message(String.format(
                "The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()))
                .build();

        return buildResponseEntity(apiError);
    }

    protected ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
