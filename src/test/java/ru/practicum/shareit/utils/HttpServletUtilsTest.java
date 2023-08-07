package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.UnknownException;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpServletUtilsTest {
    @Test
    void getURLWithParam() {
        HttpServletRequest request = null;

        UnknownException ex = assertThrows(
                UnknownException.class, () -> HttpServletUtils.getURLWithParam(request)
        );
        assertThat(ex.getMessage(), equalTo("Unknown [HttpServletRequest Null]"));
    }
}
