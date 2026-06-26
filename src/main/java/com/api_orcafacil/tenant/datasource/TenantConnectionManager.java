package com.api_orcafacil.tenant.datasource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.api_orcafacil.tenant.TenantDescriptor;
import com.zaxxer.hikari.HikariDataSource;

@Component
public class TenantConnectionManager {

    private final TenantDataSourceFactory dataSourceFactory;
    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public TenantConnectionManager(TenantDataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public DataSource obterOuCriar(TenantDescriptor descriptor) {
        if (descriptor.databaseName() == null || descriptor.databaseName().isBlank()) {
            throw new IllegalArgumentException("Database do tenant nao informado");
        }
        return dataSources.computeIfAbsent(descriptor.databaseName(), ignored -> dataSourceFactory.criar(descriptor));
    }

    public void invalidar(String databaseName) {
        DataSource dataSource = dataSources.remove(databaseName);
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.close();
        }
    }
}
