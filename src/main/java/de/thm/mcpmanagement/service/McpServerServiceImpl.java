package de.thm.mcpmanagement.service;


import de.thm.mcpmanagement.client.UserManagementClient;
import de.thm.mcpmanagement.client.exception.ClientAuthenticationException;
import de.thm.mcpmanagement.client.exception.ClientNotFoundException;
import de.thm.mcpmanagement.dto.GetApiListResponseDto;
import de.thm.mcpmanagement.entity.GroMoKoSoMcpServer;
import de.thm.mcpmanagement.entity.GroMoKoSoMcpServerProvider;
import de.thm.mcpmanagement.entity.ToolSet;
import de.thm.mcpmanagement.repository.ToolSetRepository;
import de.thm.mcpmanagement.service.exception.ServiceExceptionHandler;
import de.thm.mcpmanagement.service.exception.ServiceNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Manges {@link GroMoKoSoMcpServer} instances of users
 *
 * @author Josia Menger
 */
@Service
public class McpServerServiceImpl implements McpServerService {

    private static final Logger logger = LoggerFactory.getLogger(McpServerServiceImpl.class);

    private final GroMoKoSoMcpServerProvider serverProvider;
    private final ToolSetRepository toolSetRepository;
    private final UserManagementClient userManagementClient;
    private final CacheManager cacheManager;
    private final String version;

    public McpServerServiceImpl(GroMoKoSoMcpServerProvider serverProvider,
                                ToolSetRepository toolSetRepository,
                                UserManagementClient userManagementClient,
                                CacheManager cacheManager,
                                @Value("${spring.application.version}") String version) {
        this.serverProvider = serverProvider;
        this.toolSetRepository = toolSetRepository;
        this.userManagementClient = userManagementClient;
        this.cacheManager = cacheManager;
        this.version = version;
    }

    public boolean isServerForUserRunning(@NonNull String username) {
        Cache cache = cacheManager.getCache("servers");
        return cache != null && cache.get(username) != null;
    }

    /**
     * Create a new mcp server for a user.
     * <p>
     * Uses Spring cache to store created instances.
     * Instances get destroyed, when not active for the last 15 minutes.
     * @param username User that owns the server
     * @return mcp server of the user
     */
    @NonNull
    @Override
    @Cacheable(value = "servers", key = "#username")
    public GroMoKoSoMcpServer getServerForUser(@NonNull String username) {
        Assert.hasText(username, "username must not be empty");

        logger.info("No server found for user {}. Building a new one", username);

        try {
            List<Integer> apis = userManagementClient.getApisFromUser(username, true);

            var builder = serverProvider.builder().serverInfo("Server of %s".formatted(username), version);
            int n = 0;
            for (var api : apis) {
                ToolSet toolSet = toolSetRepository.findById(api).orElse(null);
                if (toolSet == null) {
                    logger.warn("Tool set with id {} not found, skipping", api);
                    continue;
                }
                builder.addToolSet(toolSet);
                n++;
            }
            logger.info("Created new server for user {} with {} tool sets", username, n);
            return builder.build();

        } catch (ClientAuthenticationException | ClientNotFoundException e) {
            throw new ServiceNotFound("Could not find apis for user. Does the user exist?", e);
        } catch (Exception e) {
            throw ServiceExceptionHandler.handleException(e);
        }
    }

}
