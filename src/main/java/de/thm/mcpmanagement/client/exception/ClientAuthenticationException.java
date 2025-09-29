package de.thm.mcpmanagement.client.exception;

public class ClientAuthenticationException extends RuntimeException {
    public ClientAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
