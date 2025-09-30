package de.thm.mcpmanagement.service.exception;

public class ServiceNotFound extends RuntimeException {
    public ServiceNotFound(String message) {
        super(message);
    }

    public ServiceNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
