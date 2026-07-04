package com.api_orcafacil.tenant.flyway;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.saas.tenant-db.flyway.enabled", havingValue = "true", matchIfMissing = true)
public class TenantFlywayMigrationService {

    private static final Logger log = LoggerFactory.getLogger(TenantFlywayMigrationService.class);

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String migrationsLocation;
    private final Map<String, Object> locksPorDatabase = new ConcurrentHashMap<>();

    public TenantFlywayMigrationService(
            @Value("${app.saas.tenant-db.host:localhost}") String host,
            @Value("${app.saas.tenant-db.port:5432}") int port,
            @Value("${app.saas.tenant-db.username:postgres}") String username,
            @Value("${app.saas.tenant-db.password:postgres}") String password,
            @Value("${app.saas.provisioning.migrations-location:classpath:db/migration}") String migrationsLocation) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.migrationsLocation = migrationsLocation;
    }

    public void migrar(String databaseName) {
        if (databaseName == null || databaseName.isBlank()) {
            return;
        }
        Object lock = locksPorDatabase.computeIfAbsent(databaseName, ignored -> new Object());
        synchronized (lock) {
            try {
                int aplicadas = Flyway.configure()
                        .dataSource(jdbcUrl(databaseName), username, password)
                        .locations(migrationsLocation)
                        .baselineOnMigrate(true)
                        .baselineVersion("0")
                        .load()
                        .migrate()
                        .migrationsExecuted;
                if (aplicadas > 0) {
                    log.info("Flyway aplicou {} migration(s) no tenant {}", aplicadas, databaseName);
                }
            } catch (Exception ex) {
                throw new IllegalStateException("Falha ao migrar database do tenant " + databaseName, ex);
            }
        }
    }

    private String jdbcUrl(String databaseName) {
        return "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
    }
}
