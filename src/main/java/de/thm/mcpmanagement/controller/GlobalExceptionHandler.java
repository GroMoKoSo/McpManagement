package de.thm.mcpmanagement.controller;

import de.thm.mcpmanagement.service.exception.ServiceError;
import de.thm.mcpmanagement.service.exception.ServiceNotAllowed;
import de.thm.mcpmanagement.service.exception.ServiceNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(OAuth2AuthenticationException.class)
    public Map<String, String> handleOAuth2AuthenticationException(OAuth2AuthenticationException ex) {
        return Map.of("error", "Unauthorized", "message", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ServiceNotAllowed.class)
    public Map<String, String> handleServiceNotAllowedException(ServiceNotAllowed ex) {
        return Map.of("error", "Operation forbidden", "message", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ServiceNotFound.class)
    public Map<String, String> handleServiceNotFoundException(ServiceNotFound ex) {
        return Map.of("error", "Resource not found", "message", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceError.class)
    public Map<String, String> handleServiceErrorException(ServiceError ex) {
        return Map.of("error", "Internal server error", "message", ex.getMessage());
    }
}
