package com.api_orcafacil.notificacao.dto;

import org.springframework.util.StringUtils;

public record NotificacaoCredenciais(Long idOrganizacaoNotificacao, String apiKey) {

    public boolean usaApiKey() {
        return StringUtils.hasText(apiKey);
    }
}
