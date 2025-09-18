package de.thm.mcpmanagement.controller;

import de.thm.mcpmanagement.service.McpServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Optional;

@Component
public class McpRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(McpRequestHandler.class);


    private final McpServerService mcpServerService;

    public McpRequestHandler(McpServerService mcpServerService) {
        this.mcpServerService = mcpServerService;
    }

    public ServerResponse handleRequest(ServerRequest request) throws Exception {
        String path = request.path();
        String auth = request.headers().firstHeader("Authorization");
        logger.debug("McpHandler auth={}", auth);
        logger.debug("McpHandler path={}", path);


        Optional<HandlerFunction<ServerResponse>> handlerFunctionOptional = mcpServerService.getProviders().get("test").getRouterFunction().route(request);
        if (handlerFunctionOptional.isPresent()) {
            logger.info("Found router function: Call handler for path");
            return handlerFunctionOptional.get().handle(request);
        }
        logger.warn("No HandlerFunction found for path={}", path);
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}