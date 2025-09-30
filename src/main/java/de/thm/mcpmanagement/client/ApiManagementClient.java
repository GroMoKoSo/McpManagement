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
        this.client = RestClient.create(baseUrl);
    }

    public InvokeApiResponseDto invokeApi(int apiId,
                                          boolean isGroupApi, String accessVia,
                                          @NonNull InvokeApiDto invokeApiDto) {
        try {
            String query = isGroupApi ? "group" : "user";
            // TODO Add query parameter for group or user
            return client.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("apis/{apiId}/invoke")
                            .queryParam(query, accessVia)
                            .build(apiId)
                    )
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invokeApiDto)
                    .retrieve().body(InvokeApiResponseDto.class);
        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
        }
    }
}
