package com.api_orcafacil.context;

import com.api_orcafacil.domain.sistema.model.MultiTenantEntity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class TenantListener {

    @PrePersist
    @PreUpdate
    public void setTenant(MultiTenantEntity entity) {
        entity.setId_tenant(TenantContext.getTenantId());
    }
}
