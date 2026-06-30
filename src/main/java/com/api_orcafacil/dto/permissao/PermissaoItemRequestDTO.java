package com.api_orcafacil.dto.permissao;

import jakarta.validation.constraints.NotBlank;

public record PermissaoItemRequestDTO(
        @NotBlank String modulo,
        @NotBlank String acao,
        String descricao) {
}
