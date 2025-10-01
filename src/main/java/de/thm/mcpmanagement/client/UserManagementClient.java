package de.thm.mcpmanagement.client;

import de.thm.mcpmanagement.client.exception.ClientExceptionHandler;
import de.thm.mcpmanagement.dto.GetApiListResponseDto;
import de.thm.mcpmanagement.security.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class UserManagementClient {

    private final TokenProvider tokenProvider;
    private final RestClient client;

    public UserManagementClient(TokenProvider tokenProvider,
                                @Value("${spring.subservices.user-management.url}") String baseUrl) {
        this.tokenProvider = tokenProvider;
        this.client = RestClient.create(baseUrl);
    }


    public List<Integer> getApisFromUser(String username, Boolean active) {
        return getApisFromUserRaw(username)
                .stream().filter(a -> active == null || a.active() == active)
                .map(GetApiListResponseDto::apiId).toList();
    }

    public String getApiOrigin(String username, int apiId) {
        for (var api : getApisFromUserRaw(username)) {
            if (api.apiId() == apiId) return api.accessVia();
        }
        try {
            for (var api : getAllApisRaw()) {
                if (api.apiId() == apiId) return api.accessVia();
            }
        } catch (Exception ignored) {}
        return null;
    }

    public List<GetApiListResponseDto> getAllApisRaw() {
        try {
            List<GetApiListResponseDto> apis = client.get()
                    .uri("/users/apis")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getToken())
                    .retrieve().body(new ParameterizedTypeReference<>() {});
            if (apis == null)
                throw new NullPointerException("Request was successful, but request could not be parsed or was null");
            return apis;
        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
        }
    }

    private List<GetApiListResponseDto> getApisFromUserRaw(String username) {
        try {
            List<GetApiListResponseDto> apis = client.get()
                    .uri("/users/{username}/apis", username)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getToken())
                    .retrieve().body(new ParameterizedTypeReference<>() {});
            if (apis == null)
                throw new NullPointerException("Request was successful, but request could not be parsed or was null");
            return apis;
        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
        }
    }
}