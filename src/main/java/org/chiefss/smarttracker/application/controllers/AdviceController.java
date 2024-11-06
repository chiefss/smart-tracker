package org.chiefss.smarttracker.application.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.chiefss.smarttracker.application.exceptions.ObjectNotFoundException;
import org.chiefss.smarttracker.application.notifications.messages.AccessDeniedMailMessage;
import org.chiefss.smarttracker.application.notifications.messages.InternalServerErrorMailMessage;
import org.chiefss.smarttracker.application.services.MailService;
import org.chiefss.smarttracker.application.utils.Defines;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Log4j2
@ControllerAdvice
@RequiredArgsConstructor
public class AdviceController {

    private final MailService mailService;

    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handle(HttpServletRequest request, AccessDeniedException accessDeniedException) {
        log.warn("Access denied (403) for request: \"{}\" \"{}\" error message: \"{}\"",
                request.getMethod(), request.getRequestURL(), accessDeniedException.getMessage());

        AccessDeniedMailMessage accessDeniedMailMessage = new AccessDeniedMailMessage();
        mailService.sendAdmin(accessDeniedMailMessage.getSubject(request), accessDeniedMailMessage.getBody(request, accessDeniedException));

        return new ModelAndView(Defines.WEB_ERROR_TEMPLATE_NAME);
    }

    @ExceptionHandler(value = {ObjectNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handle(HttpServletRequest request, ObjectNotFoundException notFoundException) {
        log.debug("Object not found (404) for request: \"{}\" \"{}\" error message: \"{}\"",
                request.getMethod(), request.getRequestURL(), notFoundException.getMessage());

        return new ModelAndView(Defines.WEB_ERROR_TEMPLATE_NAME);
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handle(HttpServletRequest request, NoHandlerFoundException notFoundException) {
        log.debug("Endpoint not found (404) for request: \"{}\" \"{}\" error message: \"{}\"",
                request.getMethod(), request.getRequestURL(), notFoundException.getMessage());

        return new ModelAndView(Defines.WEB_ERROR_TEMPLATE_NAME);
    }

    @ExceptionHandler(value = {NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handle(HttpServletRequest request, NoResourceFoundException notFoundException) {
        log.debug("Resource not found (404) for request: \"{}\" \"{}\" error message: \"{}\"",
                request.getMethod(), request.getRequestURL(), notFoundException.getMessage());

        return new ModelAndView(Defines.WEB_ERROR_TEMPLATE_NAME);
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handle(HttpServletRequest request, Exception exception) {
        log.error("An error (500) occurred for request: \"{}\" \"{}\" error message: \"{}\"",
                request.getMethod(), request.getRequestURL(), exception.getMessage());

        InternalServerErrorMailMessage internalServerErrorMailMessage = new InternalServerErrorMailMessage();
        mailService.sendAdmin(internalServerErrorMailMessage.getSubject(request), internalServerErrorMailMessage.getBody(request, exception));

        return new ModelAndView(Defines.WEB_ERROR_TEMPLATE_NAME);
    }
}
