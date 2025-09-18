package de.thm.mcpmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController()
public class WellKnownMetadataController {

    @GetMapping(".well-known/oauth-protected-resource")
    public Map<String, Object> getProtectedResourceMetadata() {
        return Map.of(
                "resource", "http://localhost:8080/sse",
                "authorization_servers", new String[]{"http://localhost:9000/realms/GroMoKoSo"}
        );
    }
}