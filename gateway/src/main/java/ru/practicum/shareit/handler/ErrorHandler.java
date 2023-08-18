package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static ru.practicum.shareit.constants.NamesInController.ERROR_MESSAGE;
import static ru.practicum.shareit.constants.NamesInController.ERROR_M_VALIDATION;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        ERROR_MESSAGE, ERROR_M_VALIDATION
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Map<String, String>> handleIllegalArgument(final IllegalArgumentException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Unknown state: UNSUPPORTED_STATUS"
                ));
    }

    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<Map<String, String>> handleThrowable(final Throwable e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        ERROR_MESSAGE, "Unknown"
                ));
    }

}
