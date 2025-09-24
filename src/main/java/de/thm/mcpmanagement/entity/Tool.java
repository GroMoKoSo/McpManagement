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
    private String title;
    private String description;
    private String requestMethod;
    private String endpoint;
    private String inputSchema;

    public Tool(String title, String description, String requestMethod, String endpoint, String inputSchema) {
        this.title = title;
        this.description = description;
        this.requestMethod = requestMethod;
        this.endpoint = endpoint;
        this.inputSchema = inputSchema;
    }

    public String getName() {
        return title.toLowerCase().replace(" ", "_");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tool tool = (Tool) o;
        return toolSet.equals(tool.toolSet)
                && title.equals(tool.title)
                && description.equals(tool.description)
                && requestMethod.equals(tool.requestMethod)
                && endpoint.equals(tool.endpoint)
                && inputSchema.equals(tool.inputSchema);
    }

    @Override
    public int hashCode() {
        int result = toolSet.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + requestMethod.hashCode();
        result = 31 * result + endpoint.hashCode();
        result = 31 * result + inputSchema.hashCode();
        return result;
    }
}
