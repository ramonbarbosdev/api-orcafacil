package com.api_orcafacil.dto;

import jakarta.validation.constraints.NotNull;

public record SelecionarOrganizacaoRequestDTO(@NotNull Long idOrganizacao) {
}
