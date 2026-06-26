package com.api_orcafacil.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class TenantEntity extends AuditableEntity {

    @Column(name = "id_organizacao", nullable = false)
    private Long idOrganizacao;
}
