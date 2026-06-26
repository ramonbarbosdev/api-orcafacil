package com.api_orcafacil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ModuloPermissaoRequestDTO(
        @NotBlank
        @Size(min = 2, max = 80)
        @Pattern(regexp = "^[a-z][a-z0-9-]*$", message = "Use letras minusculas, numeros e hifen")
        String codigoModulo,
        @NotBlank @Size(min = 2, max = 120) String nmModulo) {
}
