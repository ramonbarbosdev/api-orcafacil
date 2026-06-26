package com.api_orcafacil.dto;

import java.util.List;

public record ModuloPermissaoAdminDTO(
        String modulo,
        String nmModulo,
        boolean flAtivo,
        int totalPermissoes,
        List<PermissaoItemDTO> permissoes) {
}
