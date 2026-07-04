package com.api_orcafacil.notificacao.dto;

public record NotificacaoApiErrorResponse(
        int status,
        String mensagem,
        String erro,
        String path) {
}
