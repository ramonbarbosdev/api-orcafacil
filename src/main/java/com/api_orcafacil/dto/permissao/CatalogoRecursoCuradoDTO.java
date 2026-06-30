package com.api_orcafacil.dto.permissao;

import java.util.List;

public record CatalogoRecursoCuradoDTO(
        String modulo,
        String label,
        String rota,
        String grupo,
        List<String> acoesSugeridas) {
}
