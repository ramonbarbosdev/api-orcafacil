package com.api_orcafacil.tenant.datasource;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnProperty(name = "app.saas.routing.enabled", havingValue = "true")
public class TenantRoutingDataSourceConfig {

    @Bean(name = "dataSource")
    @Primary
    DataSource dataSource(TenantConnectionManager tenantConnectionManager) {
        return new TenantAwareRoutingDataSource(tenantConnectionManager);
    }
}
