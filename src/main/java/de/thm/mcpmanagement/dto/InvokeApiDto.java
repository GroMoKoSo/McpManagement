package de.thm.mcpmanagement.dto;

public record InvokeApiDto(String requestType,
                           String requestPath,
                           Object header,
                           Object body,
                           Object requestParam,
                           Object pathParam) { }

