package com.api_orcafacil.dto;

import java.util.List;

public record PermissaoModuloDTO(
        String modulo,
        String nmModulo,
        List<PermissaoItemDTO> permissoes) {
}
