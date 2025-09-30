package de.thm.mcpmanagement.client.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.client.HttpClientErrorException;

public class ClientExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientExceptionHandler.class);

    public static RuntimeException handleException(Exception e) {
        if (e instanceof OAuth2AuthenticationException) {
            logger.error("Authentication Exception: ", e);
            return new ClientAuthenticationException("Authentication Failed", e);

        } else if (e instanceof HttpClientErrorException) {
            logger.error("Client Error Exception: ", e);
            HttpStatusCode status = ((HttpClientErrorException) e).getStatusCode();

            if (status == HttpStatus.UNAUTHORIZED) return new ClientAuthenticationException("Authentication Failed", e);
            else if (status == HttpStatus.NOT_FOUND) return new ClientNotFoundException("Resource not found", e);
            else return new ClientErrorException(e.getMessage(), e);

        } else {
            logger.error("Other Exception: ", e);
            return new ClientErrorException(e.getMessage(), e);
        }
    }
}
