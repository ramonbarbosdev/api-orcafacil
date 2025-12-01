package com.api_orcafacil.context;

import com.api_orcafacil.model.entity.MultiTenantEntity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class TenantListener {

    @PrePersist
    @PreUpdate
    public void setTenant(MultiTenantEntity entity) {
        entity.setId_tenant(TenantContext.getTenantId());
    }
}
