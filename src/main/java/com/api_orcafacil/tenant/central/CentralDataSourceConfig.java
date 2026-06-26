package com.api_orcafacil.tenant.central;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class CentralDataSourceConfig {

    @Bean(name = "centralDataSource")
    DataSource centralDataSource(
            @Value("${app.saas.central.url}") String url,
            @Value("${app.saas.central.username}") String username,
            @Value("${app.saas.central.password}") String password,
            @Value("${app.saas.central.maximum-pool-size:5}") int maximumPoolSize,
            @Value("${app.saas.central.create-database.enabled:true}") boolean createDatabaseEnabled,
            @Value("${app.saas.central.create-database.admin-database:postgres}") String adminDatabase) {
        if (createDatabaseEnabled) {
            criarDatabaseSeNaoExiste(url, username, password, adminDatabase);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setPoolName("saas-central");
        return new HikariDataSource(config);
    }

    @Bean(name = "centralJdbcTemplate")
    JdbcTemplate centralJdbcTemplate(@Qualifier("centralDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "centralNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate centralNamedParameterJdbcTemplate(
            @Qualifier("centralJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Bean(name = "centralFlyway")
    Flyway centralFlyway(
            @Qualifier("centralDataSource") DataSource dataSource,
            @Value("${app.saas.central.flyway.locations:classpath:db/central}") String locations) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load();
    }

    @Bean(name = "centralFlywayMigrator")
    InitializingBean centralFlywayMigrator(
            @Qualifier("centralFlyway") Flyway flyway,
            @Value("${app.saas.central.flyway.enabled:true}") boolean enabled) {
        return () -> {
            if (enabled) {
                flyway.migrate();
            }
        };
    }

    private void criarDatabaseSeNaoExiste(String centralUrl, String username, String password, String adminDatabase) {
        String databaseName = extrairDatabaseName(centralUrl);
        validarDatabaseName(databaseName);
        String adminUrl = trocarDatabase(centralUrl, adminDatabase);

        try (Connection connection = DriverManager.getConnection(adminUrl, username, password);
                PreparedStatement existsStatement = connection.prepareStatement(
                        "select 1 from pg_database where datname = ?");
                Statement statement = connection.createStatement()) {
            connection.setAutoCommit(true);
            existsStatement.setString(1, databaseName);
            try (ResultSet rs = existsStatement.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
            statement.execute("create database \"" + databaseName.replace("\"", "\"\"") + "\"");
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao criar database central " + databaseName, ex);
        }
    }

    private String extrairDatabaseName(String jdbcUrl) {
        int queryIndex = jdbcUrl.indexOf('?');
        String semQuery = queryIndex >= 0 ? jdbcUrl.substring(0, queryIndex) : jdbcUrl;
        int slashIndex = semQuery.lastIndexOf('/');
        if (slashIndex < 0 || slashIndex == semQuery.length() - 1) {
            throw new IllegalArgumentException("URL JDBC central sem database: " + jdbcUrl);
        }
        return semQuery.substring(slashIndex + 1);
    }

    private String trocarDatabase(String jdbcUrl, String databaseName) {
        int queryIndex = jdbcUrl.indexOf('?');
        String query = queryIndex >= 0 ? jdbcUrl.substring(queryIndex) : "";
        String semQuery = queryIndex >= 0 ? jdbcUrl.substring(0, queryIndex) : jdbcUrl;
        int slashIndex = semQuery.lastIndexOf('/');
        return semQuery.substring(0, slashIndex + 1) + databaseName + query;
    }

    private void validarDatabaseName(String databaseName) {
        if (databaseName == null || !databaseName.toLowerCase(Locale.ROOT).matches("[a-z0-9_]+")) {
            throw new IllegalArgumentException("Nome de database central invalido: " + databaseName);
        }
    }
}
