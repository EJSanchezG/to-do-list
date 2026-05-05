package com.practicandospring.todolist.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Validated
public record InfoAppConfig(
        @NotBlank(message = "El nombre del desarrollador no puede estar vacío")
        String developer,
        @Pattern(regexp = "^[0-9]+\\.[0-9]+\\.[0-9]+$", message = "Formato de versión inválido (X.X.X)")
        String version
) {
}
