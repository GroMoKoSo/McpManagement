package de.thm.mcpmanagement.client;

import de.thm.mcpmanagement.client.exception.ClientExceptionHandler;
import de.thm.mcpmanagement.dto.InvokeApiDto;
import de.thm.mcpmanagement.dto.InvokeApiResponseDto;
import de.thm.mcpmanagement.security.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * ApiManagementClient is responsible for communicating with the McpManagementClient subsystem
 *
 * @author Josia Menger
 */
@Component
public class ApiManagementClient {

    private final TokenProvider tokenProvider;
    private final RestClient client;
    private final String baseUrl;

    public ApiManagementClient(TokenProvider tokenProvider,
                               @Value("${spring.subservices.api-management.url}") String baseUrl) {
        this.tokenProvider = tokenProvider;
        this.baseUrl = baseUrl;
        this.client = RestClient.create();
    }

    public InvokeApiResponseDto invokeApi(int apiId, @NonNull InvokeApiDto invokeApiDto) {
        try {
            return client.post()
                    .uri(baseUrl + "/apis/{apiId}/invoke", apiId)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invokeApiDto)
                    .retrieve().body(InvokeApiResponseDto.class);
        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
        }
    }
}
