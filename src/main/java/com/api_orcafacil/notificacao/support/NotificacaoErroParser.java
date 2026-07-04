package com.api_orcafacil.notificacao.support;

import org.springframework.web.client.RestClientResponseException;

import com.api_orcafacil.notificacao.dto.NotificacaoApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class NotificacaoErroParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private NotificacaoErroParser() {
    }

    public static String extrairMensagem(RestClientResponseException ex) {
        int status = ex.getStatusCode().value();
        if (status == 401) {
            return "API Key invalida ou expirada na notificacao-api. "
                    + "Use a chave completa no formato nak_prefixo.segredo (nao apenas o prefixo).";
        }
        if (status == 403) {
            return "Acesso negado na notificacao-api (403). Verifique: "
                    + "(1) NOTIFICACAO_API_KEY com a chave completa (nak_xxx.segredo); "
                    + "(2) scopes NOTIFICACOES_ENVIAR e CONTATOS_GERENCIAR; "
                    + "(3) notificacao-api rodando em NOTIFICACAO_BASE_URL.";
        }
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
