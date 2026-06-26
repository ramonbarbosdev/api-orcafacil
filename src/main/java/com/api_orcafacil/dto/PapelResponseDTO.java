package com.api_orcafacil.dto;

public record PapelResponseDTO(
        Long idPapel,
        String nmPapel,
        boolean flAtivo,
        int totalPermissoes) {
}
