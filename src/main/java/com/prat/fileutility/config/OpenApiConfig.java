package com.prat.fileutility.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI documentation.
 * This class configures the OpenAPI documentation for the FileUtilityService API.
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    /**
     * Configures the OpenAPI documentation.
     *
     * @return the OpenAPI configuration
     */
    @Bean
    public OpenAPI fileUtilityServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " API Documentation")
                        .description("RESTful API documentation for the FileUtilityService application. This service provides various file utility operations including uploading, downloading, resizing, and deleting files.")
                        .version(applicationVersion)
                        .contact(new Contact()
                                .name("FileUtilityService Support")
                                .email("support@fileutilityservice.com")
                                .url("https://www.fileutilityservice.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.fileutilityservice.com")
                                .description("Production server")));
    }
}