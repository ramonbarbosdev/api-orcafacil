package com.api_orcafacil.dto;

import java.util.List;

import com.api_orcafacil.dto.plano.PoliticaPlanoResumoDTO;

public record MeResponseDTO(
        Long idUsuario,
        String tipoGlobal,
        Long idOrganizacao,
        String role,
        List<String> permissoes,
        PoliticaPlanoResumoDTO politicaPlano) {
}
