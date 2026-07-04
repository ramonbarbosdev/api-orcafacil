package com.api_orcafacil.notificacao.dto;

public record NotificacaoLoginResponse(
        String token,
        String tipoGlobal,
        Boolean deveSelecionarOrganizacao) {
}
