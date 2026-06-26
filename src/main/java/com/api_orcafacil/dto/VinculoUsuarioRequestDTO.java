package com.api_orcafacil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VinculoUsuarioRequestDTO(
        @NotBlank @Size(min = 11, max = 11) String nuCpf,
        @NotBlank String nmUsuario,
        @NotBlank String dsSenha,
        @NotNull String dsRole) {
}
