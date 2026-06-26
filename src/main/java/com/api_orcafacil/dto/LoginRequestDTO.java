package com.api_orcafacil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank @Size(min = 11, max = 11) String nuCpf,
        @NotBlank String dsSenha) {
}
