package com.api_orcafacil.dto.organizacao;

import java.time.LocalDateTime;

public record OrganizacaoLogoMetadadosDTO(
        boolean possuiLogo,
        String url,
        String contentType,
        Long tamanhoBytes,
        Integer largura,
        Integer altura,
        LocalDateTime atualizadaEm) {
}
