package com.vitorcsouza.app.task_manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .version("v1")
                        .description("""
                                REST API for managing task categories and tasks.
                                The API supports CRUD operations, pagination, validation,
                                and toggling a task's completion status.
                                """)
                        .contact(new Contact()
                                .name("Task Manager API Support")));
    }
}
