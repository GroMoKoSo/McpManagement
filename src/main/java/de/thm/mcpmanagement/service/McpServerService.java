package de.thm.mcpmanagement.service;

import de.thm.mcpmanagement.entity.GroMoKoSoMcpServer;
import org.springframework.lang.NonNull;

/**
 * This class manages {@link GroMoKoSoMcpServer} instances of users.
 *
 * @author Josia Menger
 */
public interface McpServerService {

    /**
     * Return the server instance of the given user.
     * If no instance was found create a new one with the current active tool sets.
     * @param username User that owns the server
     * @return mcp server of the user
     */
    @NonNull
    GroMoKoSoMcpServer getServerForUser(@NonNull String username);
}
