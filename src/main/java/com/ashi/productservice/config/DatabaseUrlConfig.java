package com.ashi.productservice.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseUrlConfig {

    @Bean
    @org.springframework.context.annotation.Primary
    public DataSourceProperties dataSourceProperties(Environment environment) {
        DataSourceProperties properties = new DataSourceProperties();

        String explicitJdbcUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        if (StringUtils.hasText(explicitJdbcUrl)) {
            properties.setUrl(explicitJdbcUrl);
            properties.setUsername(environment.getProperty("SPRING_DATASOURCE_USERNAME"));
            properties.setPassword(environment.getProperty("SPRING_DATASOURCE_PASSWORD"));
            return properties;
        }

        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (!StringUtils.hasText(databaseUrl)) {
            databaseUrl = System.getenv("DATABASE_URL");
        }
        if (StringUtils.hasText(databaseUrl)) {
            DatabaseSettings settings = parseDatabaseUrl(databaseUrl);
            properties.setUrl(settings.url());
            properties.setUsername(settings.username());
            properties.setPassword(settings.password());
        }

        return properties;
    }

    private DatabaseSettings parseDatabaseUrl(String databaseUrl) {
        if (databaseUrl.startsWith("jdbc:")) {
            return new DatabaseSettings(databaseUrl, null, null);
        }

        if (!(databaseUrl.startsWith("postgresql://") || databaseUrl.startsWith("postgres://"))) {
            return new DatabaseSettings(databaseUrl, null, null);
        }

        try {
            URI uri = new URI(databaseUrl);
            String username = null;
            String password = null;
            if (uri.getUserInfo() != null) {
                String[] parts = uri.getUserInfo().split(":", 2);
                username = parts[0];
                if (parts.length > 1) {
                    password = parts[1];
                }
            }

            String query = uri.getRawQuery();
            String jdbcUrl = "jdbc:postgresql://" + uri.getHost();
            if (uri.getPort() > 0) {
                jdbcUrl += ":" + uri.getPort();
            }
            jdbcUrl += uri.getPath();
            if (StringUtils.hasText(query)) {
                jdbcUrl += "?" + query;
            }

            return new DatabaseSettings(jdbcUrl, username, password);
        } catch (URISyntaxException exception) {
            return new DatabaseSettings("jdbc:" + databaseUrl, null, null);
        }
    }

    private record DatabaseSettings(String url, String username, String password) {
    }
}


