package de.thm.mcpmanagement.configuration;

import de.thm.mcpmanagement.security.OAuthChallengeEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final OAuthChallengeEntryPoint challengeEntryPoint;

    public SecurityConfiguration(OAuthChallengeEntryPoint challengeEntryPoint) {
        this.challengeEntryPoint = challengeEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Protect all endpoints with OAuth2
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/sse").authenticated()
                        .anyRequest().permitAll()
                )

                // Enable OAuth2 Resource Server support
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )

                // Include redirects to the authorization server in the challenge response
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(challengeEntryPoint))

                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

}
