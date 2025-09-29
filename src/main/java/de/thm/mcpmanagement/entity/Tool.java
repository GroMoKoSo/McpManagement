package de.thm.mcpmanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tool {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne
    @JoinColumn(name = "tool_id")
    @JsonBackReference
    private ToolSet toolSet;
    private String name;
    private String description;
    private String requestMethod;
    private String endpoint;
    @Lob
    private String inputSchema;

    public Tool(String name, String description, String requestMethod, String endpoint, String inputSchema) {
        this.name = name;
        this.description = description;
        this.requestMethod = requestMethod;
        this.endpoint = endpoint;
        this.inputSchema = inputSchema;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tool tool = (Tool) o;
        return toolSet.equals(tool.toolSet)
                && name.equals(tool.name)
                && description.equals(tool.description)
                && requestMethod.equals(tool.requestMethod)
                && endpoint.equals(tool.endpoint)
                && inputSchema.equals(tool.inputSchema);
    }

    @Override
    public int hashCode() {
        int result = toolSet.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + requestMethod.hashCode();
        result = 31 * result + endpoint.hashCode();
        result = 31 * result + inputSchema.hashCode();
        return result;
    }
}
