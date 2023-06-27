package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ApplicationException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ApplicationException.class)
    protected ResponseEntity<Object> handleApplicationException(ApplicationException e) {
        HttpStatus responseStatus = e.getResponseStatus();
        String message = e.getErrorMessage();

        if (responseStatus.is4xxClientError()) {
            log.warn(message);
        } else if (responseStatus.is5xxServerError()) {
            log.error(message);
        } else {
            log.debug(message);
        }

        return ResponseEntity
                .status(e.getResponseStatus())
                .body(Map.of("errorMessage", message));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected Map<String, String> handleValidationException(MethodArgumentNotValidException e) {
        log.debug(e.getMessage());

        return Map.of(
                "errorMessage", "Validation error"
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    protected Map<String, String> handleThrowable(final Throwable e) {
        log.warn(e.getMessage());

        return Map.of(
                "errorMessage", "Unknown"
        );
    }
}
