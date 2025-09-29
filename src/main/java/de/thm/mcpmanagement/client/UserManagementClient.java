package de.thm.mcpmanagement.client;

import de.thm.mcpmanagement.client.exception.ClientExceptionHandler;
import de.thm.mcpmanagement.dto.GetApiListResponseDto;
import de.thm.mcpmanagement.security.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class UserManagementClient {

    private final TokenProvider tokenProvider;
    private final RestClient client;
    private final String baseUrl;

    public UserManagementClient(TokenProvider tokenProvider,
                                @Value("${spring.subservices.user-management.url}") String baseUrl) {
        this.tokenProvider = tokenProvider;
        this.baseUrl = baseUrl;
        this.client = RestClient.create();
    }

    public List<Integer> getApisFromUser(String username, Boolean active) {
        try {
            List<GetApiListResponseDto> apis = client.get()
                    .uri(baseUrl + "/users/{username}/apis", username)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .retrieve().body(new ParameterizedTypeReference<>() {});
            if (apis == null)
                throw new NullPointerException("Request was successful, but request could not be parsed or was null");
            return apis.stream().filter(a -> active == null || a.active() == active)
                    .map(GetApiListResponseDto::apiId).toList();
        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
        }
    }
}