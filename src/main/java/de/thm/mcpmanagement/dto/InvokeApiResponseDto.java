package de.thm.mcpmanagement.dto;

import org.springframework.http.HttpStatus;

import java.util.Map;

public record InvokeApiResponseDto(
        String responseCode,
        Map<String, String> headers,
        Map<String, Object> body
) {
    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(Integer.parseInt(responseCode));
    }
}
