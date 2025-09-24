package de.thm.mcpmanagement.client;

import de.thm.mcpmanagement.dto.GetApiListResponseDto;
import de.thm.mcpmanagement.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import javax.security.sasl.AuthenticationException;
import java.util.List;

@Component
public class UserManagementClient {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementClient.class);
    private final TokenProvider tokenProvider;
    private final RestClient client;
    private final String baseUrl;

    public UserManagementClient(TokenProvider tokenProvider,
                                @Value("${spring.subservices.user-management.url}") String baseUrl) {
        this.tokenProvider = tokenProvider;
        this.baseUrl = baseUrl;
        this.client = RestClient.create();
    }

    public List<GetApiListResponseDto> getApisFromUser(String username) throws AuthenticationException {
        var responseSpec = client.get()
                .uri(baseUrl + "/users/{username}/apis", username)
                .header("Authorization", "Bearer " + tokenProvider.getToken())
                .retrieve();
        logger.debug("Raw response: {}", responseSpec.body(String.class));
        return responseSpec.body(new ParameterizedTypeReference<>() {});
    }
}