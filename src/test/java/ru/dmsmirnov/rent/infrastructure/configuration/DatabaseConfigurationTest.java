package ru.dmsmirnov.rent.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatabaseConfigurationTest {

    @Autowired
    private Environment environment;

    @Test
    void datasourceProperties_areConfigured() {
        assertThat(environment.getProperty("spring.datasource.url"))
                .contains("h2");
        assertThat(environment.getProperty("spring.datasource.username"))
                .isNotBlank();
        assertThat(environment.getProperty("spring.datasource.password"))
                .isNotNull();
    }

    @Test
    void liquibaseProperties_areConfigured() {
        assertThat(environment.getProperty("spring.liquibase.change-log"))
                .isEqualTo("classpath:db/changelog/master.xml");
        assertThat(environment.getProperty("spring.liquibase.default-schema"))
                .isEqualTo("rent_service");
    }

}
