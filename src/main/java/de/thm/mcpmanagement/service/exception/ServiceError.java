package de.thm.mcpmanagement.service.exception;

public class ServiceError extends RuntimeException {
    public ServiceError(String message, Throwable cause) {
        super(message, cause);
    }
}
