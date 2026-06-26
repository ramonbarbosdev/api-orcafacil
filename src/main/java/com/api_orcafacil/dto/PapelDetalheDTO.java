package com.api_orcafacil.dto;

import java.util.List;

public record PapelDetalheDTO(
        Long idPapel,
        String nmPapel,
        List<String> chaves) {
}
