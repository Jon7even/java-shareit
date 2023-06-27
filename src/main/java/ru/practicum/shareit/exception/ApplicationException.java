package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class ApplicationException extends RuntimeException {
    String errorCode;
    String errorMessage;
    HttpStatus responseStatus;

    public ApplicationException(String errorMessage, HttpStatus responseStatus) {
        super(errorMessage);
        this.errorCode = responseStatus.toString();
        this.errorMessage = errorMessage;
        this.responseStatus = responseStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }
}
