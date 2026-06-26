package com.api_orcafacil.tenant;

import java.util.List;

public final class TenantRuntimeContext {

    private static final ThreadLocal<CurrentTenant> CURRENT = new ThreadLocal<>();

    private TenantRuntimeContext() {
    }

    public static void set(CurrentTenant tenant) {
        CURRENT.set(tenant);
    }

    public static CurrentTenant get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }

    public record CurrentTenant(
            Long idUsuario,
            Long idOrganizacao,
            String role,
            List<String> permissoes,
            TenantDescriptor descriptor) {
    }
}
