package de.thm.mcpmanagement.repository;

import de.thm.mcpmanagement.entity.ToolSet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ToolSetRepository extends CrudRepository<ToolSet, Integer> {
    List<ToolSet> findByIdIn(List<Integer> ids);
}
