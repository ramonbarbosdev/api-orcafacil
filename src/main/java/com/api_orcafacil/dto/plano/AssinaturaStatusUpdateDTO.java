package com.api_orcafacil.dto.plano;

import java.time.LocalDateTime;

import com.api_orcafacil.common.AssinaturaStatus;

public record AssinaturaStatusUpdateDTO(
        AssinaturaStatus tpStatus,
        LocalDateTime dtFim,
        LocalDateTime dtFimTrial) {
}
