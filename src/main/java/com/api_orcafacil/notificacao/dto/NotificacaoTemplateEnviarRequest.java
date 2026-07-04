package com.api_orcafacil.notificacao.dto;

import java.util.Map;

public record NotificacaoTemplateEnviarRequest(
        String templateKey,
        String destinatario,
        Map<String, String> variaveis) {
}
