package com.api_orcafacil.dto.permissao;

import jakarta.validation.constraints.NotBlank;

public record PermissaoItemUpdateDTO(
        @NotBlank String descricao,
        Boolean flAtivo) {
}
