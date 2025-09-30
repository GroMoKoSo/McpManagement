package de.thm.mcpmanagement.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.thm.mcpmanagement.dto.ToolDto;
import de.thm.mcpmanagement.dto.ToolSetDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ToolSet {
    @Id
    private int id;
    private String name;
    private String description;
    @OneToMany(mappedBy = "toolSet", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    List<Tool> tools;
    private String accessVia;
    private boolean isGroupTool;

    public ToolSet(int id, String name, String description, String accessVia, boolean isGroupTool) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tools = new ArrayList<>();
        this.accessVia = accessVia;
        this.isGroupTool = isGroupTool();
    }

    public void addTool(Tool tool) {
        tool.setToolSet(this);
        tools.add(tool);
    }

    public ToolSetDto toDto() {
        return new ToolSetDto(name, description, tools.stream().map(Tool::toDto).toArray(ToolDto[]::new));
    }
}
