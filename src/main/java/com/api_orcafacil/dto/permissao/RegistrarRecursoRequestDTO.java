package com.api_orcafacil.dto.permissao;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record RegistrarRecursoRequestDTO(
        @NotBlank String recurso,
        String descricao,
        List<String> acoes) {
}
