package com.api_orcafacil.dto;

import java.util.List;

public record MeResponseDTO(
        Long idUsuario,
        String tipoGlobal,
        Long idOrganizacao,
        String role,
        List<String> permissoes) {
}
