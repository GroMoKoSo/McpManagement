package de.thm.mcpmanagement.dto;

public record InvokeApiDto(String requestType,
                           String requestPath,
                           Object headers,
                           Object body,
                           Object requestParameter,
                           Object pathParameter) { }

