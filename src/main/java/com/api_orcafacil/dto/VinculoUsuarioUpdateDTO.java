package com.api_orcafacil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VinculoUsuarioUpdateDTO(
        @NotBlank String nmUsuario,
        String dsSenha,
        @NotNull String dsRole) {
}
