package de.thm.mcpmanagement.service.exception;

import de.thm.mcpmanagement.client.exception.ClientAuthenticationException;
import de.thm.mcpmanagement.client.exception.ClientNotFoundException;

import java.util.NoSuchElementException;

public class ServiceExceptionHandler {
    public static RuntimeException handleException(Exception e) {
        if (e instanceof ClientNotFoundException || e instanceof NoSuchElementException) {
            return new ServiceNotFound("Resource not found", e);

        } else if (e instanceof ClientAuthenticationException) {
            return new ServiceNotAllowed("Authentication failed!", e);

        } else {
            return new ServiceError(e.getMessage(), e);
        }
    }
}
