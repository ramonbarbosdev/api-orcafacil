package com.api_orcafacil.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record PermissoesUpdateDTO(
        @NotNull List<String> chaves) {
}
