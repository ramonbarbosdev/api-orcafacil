package com.api_orcafacil.dto;

import jakarta.validation.constraints.NotBlank;

public record OrganizacaoRequestDTO(
        @NotBlank String nmOrganizacao,
        String dsDocumento) {
}
