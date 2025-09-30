package de.thm.mcpmanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetApiListResponseDto(@JsonProperty("apiId") int apiId,
                                    @JsonProperty("accessVia") String accessVia,
                                    @JsonProperty("active") boolean active) {
}
