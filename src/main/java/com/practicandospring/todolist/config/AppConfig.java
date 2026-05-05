package com.practicandospring.todolist.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InfoAppConfig.class)
public class AppConfig {
}
