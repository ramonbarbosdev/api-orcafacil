package com.api_orcafacil.tenant;

public record TenantDescriptor(
        Long idOrganizacao,
        String slug,
        StorageMode storageMode,
        String databaseName,
        String databaseHostRef,
        Integer databasePort,
        OrganizationStatus status) {

    public boolean usaBancoDaOrganizacao() {
        return storageMode == StorageMode.DATABASE_PER_ORG || storageMode == StorageMode.DEDICATED_INFRA;
    }
}
