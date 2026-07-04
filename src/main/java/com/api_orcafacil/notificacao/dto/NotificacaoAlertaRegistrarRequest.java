package com.api_orcafacil.notificacao.dto;

public record NotificacaoAlertaRegistrarRequest(
        String titulo,
        String mensagem,
        String destinatario,
        String canal,
        String codigoErro,
        Long idNotificacao) {
}
