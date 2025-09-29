package de.thm.mcpmanagement.client.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
