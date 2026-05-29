package ru.dmsmirnov.rent.infrastructure.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "ru.dmsmirnov.rent.infrastructure.store.repository")
@EntityScan(basePackages = "ru.dmsmirnov.rent.infrastructure.store.entity")
public class DatabaseConfiguration {

}
