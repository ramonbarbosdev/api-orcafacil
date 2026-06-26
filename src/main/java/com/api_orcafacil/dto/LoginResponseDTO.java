package com.api_orcafacil.dto;

import java.util.List;

public record LoginResponseDTO(
        String token,
        String tipoGlobal,
        boolean precisaSelecionarOrganizacao,
        List<OrganizacaoLoginDTO> organizacoes) {
}
