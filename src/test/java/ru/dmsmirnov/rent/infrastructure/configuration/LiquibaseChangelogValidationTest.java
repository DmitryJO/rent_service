package ru.dmsmirnov.rent.infrastructure.configuration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LiquibaseChangelogValidationTest {

    @Test
    void changelogFiles_existOnClasspath() {
        var classLoader = getClass().getClassLoader();
        assertThat(classLoader.getResource("db/changelog/master.xml")).isNotNull();
        assertThat(classLoader.getResource("db/changelog/V1__create_tables.xml")).isNotNull();
        assertThat(classLoader.getResource("db/changelog/V2__add_indexes.xml")).isNotNull();
    }

}
