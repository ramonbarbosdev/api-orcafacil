package com.api_orcafacil.notificacao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.notificacao")
public class NotificacaoProperties {

    private boolean enabled = false;
    private String baseUrl = "http://localhost:8080/api";
    private String login;
    private String senha;
    /** API Key M2M (nak_xxx.chave). Preferida sobre login/senha JWT. */
    private String apiKey;
    /** ID da organizacao na notificacao-api (pode diferir do orcafacil). */
    private Long idOrganizacao;
    /** URL publica do OrcaFacil para montar links do orcamento (ex: https://app.exemplo.com/orcafacil). */
    private String publicBaseUrl = "http://localhost:8080/orcafacil";
    /** Chave de template opcional na notificacao-api (ex: orcamento-enviado). */
    private String templateOrcamentoEnviado;
    private boolean registrarConsentimentoWhatsapp = true;
    private int connectTimeoutMs = 5_000;
    private int readTimeoutMs = 30_000;
}
