package com.api_orcafacil.tenant.datasource;

import javax.sql.DataSource;

import com.api_orcafacil.tenant.TenantDescriptor;

public interface TenantDataSourceFactory {

    DataSource criar(TenantDescriptor descriptor);
}
