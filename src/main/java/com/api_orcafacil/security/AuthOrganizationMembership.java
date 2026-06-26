package com.api_orcafacil.security;

public record AuthOrganizationMembership(
        Long idUsuarioOrganizacao,
        Long idUsuario,
        Long idOrganizacao,
        String nmOrganizacao,
        String dsRole) {
}
