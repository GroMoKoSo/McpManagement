package de.thm.mcpmanagement.service.exception;

public class ServiceNotAllowed extends RuntimeException {
    public ServiceNotAllowed(String message) {
        super(message);
    }

    public ServiceNotAllowed(String message, Throwable cause) {
        super(message, cause);
    }
}
