package com.api_orcafacil.dto.plano;

import java.time.LocalDateTime;

public record AssinaturaResponseDTO(
        Long idOrganizacaoAssinatura,
        Long idOrganizacao,
        Long idPlanoAssinatura,
        String nmPlanoAssinatura,
        String tpStatus,
        LocalDateTime dtInicio,
        LocalDateTime dtFim,
        LocalDateTime dtFimTrial,
        LocalDateTime dtProximoCiclo,
        boolean flRenovacaoAutomatica) {
}
