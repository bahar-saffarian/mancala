package com.bol.mancala.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
@Builder
public class ApiError implements Serializable {

    private HttpStatus status;

    private final LocalDateTime timestamp = LocalDateTime.now();

    private String code;

    private String message;

    private List<ApiValidationError> subErrors;


    private void addSubError(ApiValidationError subError) {
        if (subErrors == null) {
            subErrors = new ArrayList<>();
        }
        subErrors.add(subError);
    }

    public void addValidationError(String field, String object,
                                   String code, String message) {
        addSubError(new ApiValidationError(field, object, code, message));
    }

    public void addValidationError(String code,
                                   String message) {
        addSubError(new ApiValidationError(code, message));
    }




    interface ApiSubError {

    }

    class ApiValidationError implements ApiSubError {

        private String object;

        private String field;

        private String code;

        private String message;


        public ApiValidationError() {
        }

        public ApiValidationError(String code,
                                  String message) {
            this.code = code;
            this.message = message;
        }

        public ApiValidationError(String field, String object,
                                  String code, String message) {
            this.field = field;
            this.object = object;
            this.code = code;
            this.message = message;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
