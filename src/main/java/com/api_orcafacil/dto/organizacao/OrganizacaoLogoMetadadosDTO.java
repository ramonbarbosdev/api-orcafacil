package com.api_orcafacil.dto.organizacao;

import java.time.LocalDateTime;

public record OrganizacaoLogoMetadadosDTO(
        boolean possuiLogo,
        String modo,
        String url,
        String logoUrlExterna,
        String contentType,
        Long tamanhoBytes,
        Integer largura,
        Integer altura,
        LocalDateTime atualizadaEm) {

    public static final String MODO_UPLOAD = "UPLOAD";
    public static final String MODO_URL = "URL";
    public static final String MODO_NENHUM = "NENHUM";
}
