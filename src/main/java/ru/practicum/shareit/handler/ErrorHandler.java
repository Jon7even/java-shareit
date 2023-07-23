package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.ApplicationException;
import ru.practicum.shareit.exception.UnknownException;

import java.util.Map;

import static ru.practicum.shareit.constants.NamesJsonResponse.ERROR_MESSAGE;
import static ru.practicum.shareit.constants.NamesJsonResponse.ERROR_M_VALIDATION;

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

        return ResponseEntity.status(e.getResponseStatus())
                .body(Map.of(ERROR_MESSAGE, message));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        log.debug(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        ERROR_MESSAGE, ERROR_M_VALIDATION
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Map<String, String>> handleThrowable(final MethodArgumentTypeMismatchException e) {
        log.warn(e.getMessage());

        Throwable cause = e.getCause().getCause();

        if (cause.getClass() == UnknownException.class) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Unknown state: UNSUPPORTED_STATUS"
                    ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        ERROR_MESSAGE, "Unknown"
                ));
    }

}
