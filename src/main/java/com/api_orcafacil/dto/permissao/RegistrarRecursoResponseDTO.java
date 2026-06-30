package com.api_orcafacil.dto.permissao;

import java.util.List;

public record RegistrarRecursoResponseDTO(
        String modulo,
        List<String> criadas,
        List<String> jaExistentes) {
}
