package ru.practicum.shareit.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class HttpServletUtils {

    private HttpServletUtils() {
    }

    public static String getURLWithParam(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString.endsWith("=")) {
            requestURL.append('?').append(queryString);
        } else {
            String decodeQuery = URLDecoder.decode(queryString, StandardCharsets.UTF_8);
            requestURL.append('?').append(decodeQuery);
        }

        return requestURL.toString();
    }

}
