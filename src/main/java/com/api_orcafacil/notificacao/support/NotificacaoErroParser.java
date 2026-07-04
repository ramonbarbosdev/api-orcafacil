package com.api_orcafacil.notificacao.support;

import org.springframework.web.client.RestClientResponseException;

import com.api_orcafacil.notificacao.dto.NotificacaoApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class NotificacaoErroParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private NotificacaoErroParser() {
    }

    public static String extrairMensagem(RestClientResponseException ex) {
        String body = ex.getResponseBodyAsString();
        if (body == null || body.isBlank()) {
            return ex.getMessage();
        }
        try {
            NotificacaoApiErrorResponse erro = MAPPER.readValue(body, NotificacaoApiErrorResponse.class);
            if (erro.mensagem() != null && !erro.mensagem().isBlank()) {
                return erro.mensagem();
            }
        } catch (Exception ignored) {
            // usa corpo bruto abaixo
        }
        return body.length() > 300 ? body.substring(0, 300) : body;
    }
}
