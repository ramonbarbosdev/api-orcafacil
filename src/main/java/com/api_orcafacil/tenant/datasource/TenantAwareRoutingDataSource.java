package com.api_orcafacil.tenant.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.AbstractDataSource;

import com.api_orcafacil.tenant.TenantRuntimeContext;

public class TenantAwareRoutingDataSource extends AbstractDataSource {

    private final TenantConnectionManager tenantConnectionManager;

    public TenantAwareRoutingDataSource(TenantConnectionManager tenantConnectionManager) {
        this.tenantConnectionManager = tenantConnectionManager;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return resolverDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return resolverDataSource().getConnection(username, password);
    }

    private DataSource resolverDataSource() {
        TenantRuntimeContext.CurrentTenant currentTenant = TenantRuntimeContext.get();
        if (currentTenant == null || currentTenant.descriptor() == null) {
            throw new IllegalStateException("Contexto de tenant obrigatorio para acessar dados operacionais");
        }
        if (!currentTenant.descriptor().usaBancoDaOrganizacao()) {
            throw new IllegalStateException("Organizacao sem database operacional habilitado");
        }
        return tenantConnectionManager.obterOuCriar(currentTenant.descriptor());
    }
}
