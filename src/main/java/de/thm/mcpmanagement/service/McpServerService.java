package de.thm.mcpmanagement.service;

import de.thm.mcpmanagement.entity.GroMoKoSoMcpServer;

public interface McpServerService {
    GroMoKoSoMcpServer getServerForUser(String username);
}
