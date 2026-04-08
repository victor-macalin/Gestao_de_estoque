package com.victor.gestao_de_estoque.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

    @Configuration
    public class DatabaseConfig {

        @Value("${PGHOST:localhost}")
        private String pgHost;

        @Value("${PGPORT:5432}")
        private String pgPort;

        @Value("${PGDATABASE:postgres}")
        private String pgDatabase;

        @Value("${PGUSER:postgres}")
        private String pgUser;

        @Value("${PGPASSWORD:}")
        private String pgPassword;

        @Bean
        public DataSource dataSource() {
            // Construir a URL JDBC dinamicamente
            String jdbcUrl = String.format(
                    "jdbc:postgresql://%s:%s/%s?sslmode=require",
                    pgHost,
                    pgPort,
                    pgDatabase
            );

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(pgUser);
            config.setPassword(pgPassword);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            return new HikariDataSource(config);
        }
    }
