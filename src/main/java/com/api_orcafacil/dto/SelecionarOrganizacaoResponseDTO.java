package com.api_orcafacil.dto;

import java.util.List;

public record SelecionarOrganizacaoResponseDTO(
        String token,
        Long idOrganizacao,
        String role,
        List<String> permissoes) {
}
