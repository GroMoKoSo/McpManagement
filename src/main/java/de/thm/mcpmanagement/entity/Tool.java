package de.thm.mcpmanagement.entity;

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
    private ToolSet toolSet;
    private String name;
    private String description;
    private String requestMethod;
    private String endpoint;
    private String inputSchema;

    public Tool(String name, String description, String requestMethod, String endpoint, String inputSchema) {
        this.name = name;
        this.description = description;
        this.requestMethod = requestMethod;
        this.endpoint = endpoint;
        this.inputSchema = inputSchema;
    }
}
