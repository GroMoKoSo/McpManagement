package de.thm.mcpmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class McpmanagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpmanagementApplication.class, args);
    }

}
