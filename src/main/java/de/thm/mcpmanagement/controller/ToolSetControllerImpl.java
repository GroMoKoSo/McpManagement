package de.thm.mcpmanagement.controller;

import de.thm.mcpmanagement.client.UserManagementClient;
import de.thm.mcpmanagement.dto.ToolSetDto;
import de.thm.mcpmanagement.entity.ToolSet;
import de.thm.mcpmanagement.security.TokenProvider;
import de.thm.mcpmanagement.service.ToolSetService;
import de.thm.mcpmanagement.service.exception.ServiceNotAllowed;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ToolSetControllerImpl implements ToolSetController {

    private final ToolSetService toolSetService;
    private final TokenProvider tokenProvider;
    private final UserManagementClient userManagementClient;

    public ToolSetControllerImpl(ToolSetService toolSetService,
                                 TokenProvider tokenProvider,
                                 UserManagementClient userManagementClient) {
        this.toolSetService = toolSetService;
        this.tokenProvider = tokenProvider;
        this.userManagementClient = userManagementClient;
    }

    @Override
    public List<ToolSetDto> getToolSets() {
        return toolSetService.getToolSets(tokenProvider.getUsernameFromToken()).stream()
                .map(ToolSet::toDto).toList();
    }

    @Override
    public ToolSetDto getToolSet(int id) {
        List<Integer> apis = userManagementClient.getApisFromUser(tokenProvider.getUsernameFromToken(), null);
        if (!apis.contains(id)) throw new ServiceNotAllowed("User is not authorized to request this resource");
        return toolSetService.getToolSets(id).toDto();
    }

    @Override
    public ResponseEntity<ToolSetDto> putToolSet(int id,
                                                 ToolSetDto toolSpecification,
                                                 HttpServletResponse response,
                                                 Authentication authentication) {
        if (toolSetService.putToolSet(id, toolSpecification, tokenProvider.getUsernameFromToken()))
            return new ResponseEntity<>(toolSpecification, HttpStatus.CREATED);
        return ResponseEntity.ok(toolSpecification);
    }

    @Override
    public void deleteToolSet(int id) {
        toolSetService.deleteToolSet(id);
    }

    @Override
    public void updateToolSetList(String id, List<Integer> apis) {
        toolSetService.updateToolSetList(id, apis);
    }
}
