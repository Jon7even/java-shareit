package ru.practicum.shareit.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.*;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ErrorHandlerTest {
    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void applicationException() {
        ApplicationException exception = new ApplicationException("test", HttpStatus.BAD_REQUEST);
        ResponseEntity<Object> result = errorHandler.handleApplicationException(exception);

        assertNotNull(result);
        assertEquals(exception.getResponseStatus(), result.getStatusCode());
        assertEquals(getError(exception.getErrorMessage()), result.getBody().toString());
    }

    @Test
    void validationException() {
        ValidationException exception = new ValidationException(Collections.singleton(Map.of("test1", "test2")));
        ResponseEntity<Object> result = errorHandler.handleApplicationException(exception);

        assertNotNull(result);
        assertEquals(exception.getResponseStatus(), result.getStatusCode());
        assertEquals(getError(exception.getMessage()), result.getBody().toString());
    }

    @Test
    void accessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("test");
        ResponseEntity<Object> result = errorHandler.handleApplicationException(exception);

        assertNotNull(result);
        assertEquals(exception.getResponseStatus(), result.getStatusCode());
        assertEquals(getError(exception.getErrorMessage()), result.getBody().toString());
    }

    @Test
    void unknownExceptionException() {
        UnknownException exception = new UnknownException("test");
        ResponseEntity<Object> result = errorHandler.handleApplicationException(exception);

        assertNotNull(result);
        assertEquals(exception.getResponseStatus(), result.getStatusCode());
        assertEquals(getError(exception.getErrorMessage()), result.getBody().toString());
    }

    @Test
    void noCompletedBookingsException() {
        NoCompletedBookingsException exception = new NoCompletedBookingsException("test");
        ResponseEntity<Object> result = errorHandler.handleApplicationException(exception);

        assertNotNull(result);
        assertEquals(exception.getResponseStatus(), result.getStatusCode());
        assertEquals(getError(exception.getErrorMessage()), result.getBody().toString());
    }

    @Test
    void incorrectParameterException() {
        IncorrectParameterException exception = new IncorrectParameterException("test");
        ResponseEntity<Object> result = errorHandler.handleApplicationException(exception);

        assertNotNull(result);
        assertEquals(exception.getResponseStatus(), result.getStatusCode());
        assertEquals(getError(exception.getErrorMessage()), result.getBody().toString());
    }

    @Test
    void entityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("test");
        ResponseEntity<Object> result = errorHandler.handleApplicationException(exception);

        assertNotNull(result);
        assertEquals(exception.getResponseStatus(), result.getStatusCode());
        assertEquals(getError(exception.getErrorMessage()), result.getBody().toString());
    }

    @Test
    void entityNotDeletedException() {
        EntityNotDeletedException exception = new EntityNotDeletedException("test");
        ResponseEntity<Object> result = errorHandler.handleApplicationException(exception);

        assertNotNull(result);
        assertEquals(exception.getResponseStatus(), result.getStatusCode());
        assertEquals(getError(exception.getErrorMessage()), result.getBody().toString());
    }

    @Test
    void eEntityAlreadyExistsException() {
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("test");
        ResponseEntity<Object> result = errorHandler.handleApplicationException(exception);

        assertNotNull(result);
        assertEquals(exception.getResponseStatus(), result.getStatusCode());
        assertEquals(getError(exception.getErrorMessage()), result.getBody().toString());
    }

    @Test
    void entityAlreadyBookedException() {
        EntityAlreadyBookedException exception = new EntityAlreadyBookedException("test");
        ResponseEntity<Object> result = errorHandler.handleApplicationException(exception);

        assertNotNull(result);
        assertEquals(exception.getResponseStatus(), result.getStatusCode());
        assertEquals(getError(exception.getErrorMessage()), result.getBody().toString());
    }

    private static String getError(String error) {
        return "{errorMessage=" + error + "}";
    }

}
