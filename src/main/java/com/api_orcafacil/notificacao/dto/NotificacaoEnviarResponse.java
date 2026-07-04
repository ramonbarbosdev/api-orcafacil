package com.api_orcafacil.notificacao.dto;

public record NotificacaoEnviarResponse(
        Boolean sucesso,
        Long idNotificacao,
        NotificacaoCanal canal,
        String status,
        String erro) {
}
