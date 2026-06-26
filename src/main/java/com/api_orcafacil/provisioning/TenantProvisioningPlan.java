package com.api_orcafacil.provisioning;

public record TenantProvisioningPlan(
        Long idOrganizacao,
        String slug,
        String databaseName) {
}
