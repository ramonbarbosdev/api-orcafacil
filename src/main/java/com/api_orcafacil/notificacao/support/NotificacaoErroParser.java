package com.api_orcafacil.notificacao.support;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import com.api_orcafacil.notificacao.dto.NotificacaoApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class NotificacaoErroParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private NotificacaoErroParser() {
    }

    public static String extrairMensagem(RestClientResponseException ex) {
        return interpretar(ex).mensagemTecnica();
    }

    public static NotificacaoErroUsuario interpretar(RestClientResponseException ex) {
        int status = ex.getStatusCode().value();
        String tecnica = extrairMensagemTecnica(ex);
        String codigo = extrairCodigoErro(ex);

        if ("WHATSAPP_NAO_CONECTADO".equals(codigo)
                || contemWhatsappNaoConectado(tecnica)) {
            return new NotificacaoErroUsuario(
                    "WHATSAPP_NAO_CONECTADO",
                    "O WhatsApp nao esta conectado. Acesse Integracoes e conecte o numero antes de enviar mensagens ao cliente.",
                    tecnica,
                    false);
        }

        return switch (status) {
            case 401 -> new NotificacaoErroUsuario(
                    "API_KEY_INVALIDA",
                    "A integração com o serviço de mensagens não está autenticada. Peça ao administrador para revisar a API Key.",
                    tecnica,
                    false);
            case 403 -> new NotificacaoErroUsuario(
                    "API_KEY_SEM_PERMISSAO",
                    "A integração não tem permissão para enviar mensagens. O administrador precisa revisar a API Key.",
                    tecnica,
                    false);
            case 404 -> new NotificacaoErroUsuario(
                    "SERVICO_NAO_ENCONTRADO",
                    "O serviço de mensagens está indisponível no momento. Tente novamente em alguns minutos.",
                    tecnica,
                    true);
            case 429 -> new NotificacaoErroUsuario(
                    "LIMITE_EXCEDIDO",
                    "Muitas mensagens foram enviadas em pouco tempo. Aguarde alguns minutos e tente novamente.",
                    tecnica,
                    false);
            case 503, 502, 504 -> new NotificacaoErroUsuario(
                    "SERVICO_INDISPONIVEL",
                    "O serviço de mensagens está temporariamente indisponível. Sua equipe foi avisada — tente novamente em breve.",
                    tecnica,
                    true);
            default -> status >= 500
                    ? new NotificacaoErroUsuario(
                            "ERRO_INTERNO_NOTIFICACAO",
                            "Ocorreu um problema interno ao enviar a mensagem. Sua equipe foi avisada — você pode tentar novamente.",
                            tecnica,
                            true)
                    : new NotificacaoErroUsuario(
                            "ERRO_ENVIO",
                            "Não foi possível enviar a mensagem agora. Verifique os dados e tente novamente.",
                            tecnica,
                            false);
        };
    }

    public static NotificacaoErroUsuario interpretarErroEnvio(String mensagem) {
        if (contemWhatsappNaoConectado(mensagem)) {
            return new NotificacaoErroUsuario(
                    "WHATSAPP_NAO_CONECTADO",
                    "O WhatsApp nao esta conectado. Acesse Integracoes e conecte o numero antes de enviar mensagens ao cliente.",
                    mensagem,
                    false);
        }
        if (mensagem != null) {
            String normalizada = mensagem.toLowerCase();
            if (normalizada.contains("pausada automaticamente")) {
                return new NotificacaoErroUsuario(
                        "WHATSAPP_SESSAO_PAUSADA",
                        "Os envios WhatsApp estao pausados. Aguarde ou reative a sessao em Integracoes.",
                        mensagem,
                        false);
            }
            if (normalizada.contains("risco operacional")) {
                return new NotificacaoErroUsuario(
                        "WHATSAPP_SESSAO_RISCO",
                        "Os envios WhatsApp estao bloqueados por protecao. Reative a sessao em Integracoes.",
                        mensagem,
                        false);
            }
            if (normalizada.contains("duplicada bloqueada")) {
                return new NotificacaoErroUsuario(
                        "MENSAGEM_DUPLICADA",
                        "Esta mensagem ja foi enviada recentemente. Aguarde antes de tentar novamente.",
                        mensagem,
                        false);
            }
        }
        return new NotificacaoErroUsuario(
                "ERRO_ENVIO",
                "Nao foi possivel enfileirar a mensagem. Verifique a integracao e tente novamente.",
                mensagem,
                false);
    }

    public static boolean deveNotificarEquipe(String codigoErro) {
        if (codigoErro == null || codigoErro.isBlank()) {
            return false;
        }
        return switch (codigoErro.trim().toUpperCase()) {
            case "WHATSAPP_NAO_CONECTADO",
                    "WHATSAPP_SESSAO_PAUSADA",
                    "WHATSAPP_SESSAO_RISCO",
                    "API_KEY_INVALIDA",
                    "API_KEY_SEM_PERMISSAO",
                    "LIMITE_EXCEDIDO",
                    "MENSAGEM_DUPLICADA",
                    "ERRO_ENVIO" -> false;
            default -> true;
        };
    }

    public static NotificacaoErroUsuario interpretarGenerico(Exception ex) {
        if (ex instanceof ResourceAccessException resourceEx) {
            Throwable cause = resourceEx.getCause();
            if (cause instanceof SocketTimeoutException) {
                return new NotificacaoErroUsuario(
                        "TIMEOUT",
                        "O serviço de mensagens demorou para responder. Sua equipe foi avisada — tente novamente em breve.",
                        ex.getMessage(),
                        true);
            }
            if (cause instanceof ConnectException) {
                return new NotificacaoErroUsuario(
                        "SERVICO_OFFLINE",
                        "Não foi possível conectar ao serviço de mensagens. Sua equipe foi avisada.",
                        ex.getMessage(),
                        true);
            }
            return new NotificacaoErroUsuario(
                    "REDE",
                    "Houve um problema de conexão com o serviço de mensagens. Tente novamente em alguns minutos.",
                    ex.getMessage(),
                    true);
        }
        return new NotificacaoErroUsuario(
                "ERRO_INESPERADO",
                "Não foi possível concluir o envio. Sua equipe foi avisada — tente novamente.",
                ex.getMessage(),
                true);
    }

    private static String extrairMensagemTecnica(RestClientResponseException ex) {
        int status = ex.getStatusCode().value();
        if (status == 401) {
            return "API Key invalida ou expirada na notificacao-api. "
                    + "Use a chave completa no formato nak_prefixo.segredo (nao apenas o prefixo).";
        }
        if (status == 403) {
            return "Acesso negado na notificacao-api (403). Verifique a API Key em Configuracoes > Integracao WhatsApp "
                    + "(formato nak_prefixo.segredo), scopes NOTIFICACOES_ENVIAR e CONTATOS_GERENCIAR, "
                    + "e se a notificacao-api esta acessivel em NOTIFICACAO_BASE_URL.";
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

    private static String extrairCodigoErro(RestClientResponseException ex) {
        String body = ex.getResponseBodyAsString();
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            NotificacaoApiErrorResponse erro = MAPPER.readValue(body, NotificacaoApiErrorResponse.class);
            return erro.erro();
        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean contemWhatsappNaoConectado(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            return false;
        }
        String normalizada = mensagem.toLowerCase();
        return normalizada.contains("whatsapp") && normalizada.contains("nao conectado")
                || normalizada.contains("whatsapp") && normalizada.contains("não conectado");
    }
}
