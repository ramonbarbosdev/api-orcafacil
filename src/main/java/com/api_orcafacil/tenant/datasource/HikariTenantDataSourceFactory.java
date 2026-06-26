package com.api_orcafacil.tenant.datasource;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.api_orcafacil.tenant.TenantDescriptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Component
public class HikariTenantDataSourceFactory implements TenantDataSourceFactory {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final int maximumPoolSize;

    public HikariTenantDataSourceFactory(
            @Value("${app.saas.tenant-db.host:localhost}") String host,
            @Value("${app.saas.tenant-db.port:5432}") int port,
            @Value("${app.saas.tenant-db.username:postgres}") String username,
            @Value("${app.saas.tenant-db.password:postgres}") String password,
            @Value("${app.saas.tenant-db.maximum-pool-size:5}") int maximumPoolSize) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.maximumPoolSize = maximumPoolSize;
    }

    @Override
    public DataSource criar(TenantDescriptor descriptor) {
        String databaseHost = descriptor.databaseHostRef() == null || descriptor.databaseHostRef().isBlank()
                ? host
                : descriptor.databaseHostRef();
        Integer databasePort = descriptor.databasePort() == null ? port : descriptor.databasePort();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + databaseHost + ":" + databasePort + "/" + descriptor.databaseName());
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setPoolName("tenant-" + descriptor.databaseName());
        return new HikariDataSource(config);
    }
}
