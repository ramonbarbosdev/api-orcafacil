package com.api_orcafacil.dto.permissao;

import java.util.List;

public record CatalogoRecursoItemDTO(
        String modulo,
        String label,
        String rota,
        String grupo,
        String origem,
        boolean cadastrado,
        boolean noCatalogoCurado,
        String status,
        List<String> acoesSugeridas,
        List<String> permissoesExistentes) {
}
