package com.api_orcafacil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModuloPermissaoUpdateDTO(
        @NotBlank @Size(min = 2, max = 120) String nmModulo,
        @NotNull Boolean flAtivo) {
}
