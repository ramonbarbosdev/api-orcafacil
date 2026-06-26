package com.api_orcafacil.dto;

import java.time.LocalDateTime;

public record OrganizacaoResponseDTO(
        Long idOrganizacao,
        String nmOrganizacao,
        String dsDocumento,
        String slug,
        String databaseName,
        String status,
        boolean flAtivo,
        LocalDateTime dtCriacao) {
}
