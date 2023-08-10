package ru.practicum.shareit.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exception.UnknownException;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class HttpServletUtils {
    public String getURLWithParam(HttpServletRequest request) {
        if (request != null) {
            StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
            String queryString = request.getQueryString();

            if (queryString.endsWith("=")) {
                requestURL.append('?').append(queryString);
            } else {
                String decodeQuery = URLDecoder.decode(queryString, StandardCharsets.UTF_8);
                requestURL.append('?').append(decodeQuery);
            }

            return requestURL.toString();
        } else {
            throw new UnknownException("HttpServletRequest Null");
        }
    }

}
