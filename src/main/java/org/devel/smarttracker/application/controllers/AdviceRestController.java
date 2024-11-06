package org.devel.smarttracker.application.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.devel.smarttracker.application.dto.AdviceRestControllerDto;
import org.devel.smarttracker.application.exceptions.ObjectNotFoundException;
import org.devel.smarttracker.application.notifications.messages.AccessDeniedMailMessage;
import org.devel.smarttracker.application.notifications.messages.InternalServerErrorMailMessage;
import org.devel.smarttracker.application.services.MailService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class AdviceRestController {

    private final MailService mailService;

    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public AdviceRestControllerDto handle(HttpServletRequest request, AccessDeniedException accessDeniedException) {
        log.warn("Access denied (403) for request: \"{}\" \"{}\" error message: \"{}\"",
                request.getMethod(), request.getRequestURL(), accessDeniedException.getMessage());

        AccessDeniedMailMessage accessDeniedMailMessage = new AccessDeniedMailMessage();
        mailService.sendAdmin(accessDeniedMailMessage.getSubject(request), accessDeniedMailMessage.getBody(request, accessDeniedException));

        return new AdviceRestControllerDto("Access Denied");
    }

    @ExceptionHandler(value = {ObjectNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AdviceRestControllerDto handle(HttpServletRequest request, ObjectNotFoundException notFoundException) {
        log.debug("Object not found (404) for request: \"{}\" \"{}\" error message: \"{}\"",
                request.getMethod(), request.getRequestURL(), notFoundException.getMessage());

        return new AdviceRestControllerDto("Object not found");
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AdviceRestControllerDto handle(HttpServletRequest request, Exception exception) {
        log.error("An error (500) occurred for request: \"{}\" \"{}\" error message: \"{}\"",
                request.getMethod(), request.getRequestURL(), exception.getMessage());

        InternalServerErrorMailMessage internalServerErrorMailMessage = new InternalServerErrorMailMessage();
        mailService.sendAdmin(internalServerErrorMailMessage.getSubject(request), internalServerErrorMailMessage.getBody(request, exception));

        return new AdviceRestControllerDto("An error occurred during request");
    }
}
