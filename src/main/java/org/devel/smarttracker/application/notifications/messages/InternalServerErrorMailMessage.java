package org.devel.smarttracker.application.notifications.messages;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

public class InternalServerErrorMailMessage {

    public static final String SUBJECT_FORMAT = "[%s] An error (500) occurred during request";
    public static final String BODY_FORMAT = """
            [%s]
                                            
            An error occurred (500) during request: "%s" "%s?%s"
                                            
            error message: "%s"
                                            
            Trace:
                                            
            %s""";

    public String getSubject(HttpServletRequest request) {
        return String.format(SUBJECT_FORMAT, request.getServerName());
    }

    public String getBody(HttpServletRequest request, Exception exception) {
        return String.format(BODY_FORMAT,
                request.getServerName(), request.getMethod(), request.getRequestURL(),
                request.getQueryString() != null ? request.getQueryString() : "",
                exception.getMessage(), Arrays.toString(exception.getStackTrace()));
    }
}