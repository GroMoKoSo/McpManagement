package de.thm.mcpmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    List<Tool> tools;

    public ToolSet(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
