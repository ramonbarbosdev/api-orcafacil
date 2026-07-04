package com.api_orcafacil.notificacao.dto;

public record NotificacaoEnviarRequest(
        NotificacaoCanal canal,
        String destinatario,
        String assunto,
        String mensagem) {
}
