package com.api_orcafacil.notificacao.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.api_orcafacil.notificacao.config.NotificacaoProperties;

@Component
@ConditionalOnProperty(name = "app.notificacao.enabled", havingValue = "true")
public class NotificacaoConfigValidator {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoConfigValidator.class);

    private final NotificacaoProperties properties;

    public NotificacaoConfigValidator(NotificacaoProperties properties) {
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void validar() {
        if (!StringUtils.hasText(properties.getBaseUrl())) {
            log.warn("Integracao notificacao: base-url nao configurada");
        }
        boolean temApiKey = StringUtils.hasText(properties.getApiKey());
        boolean temJwt = StringUtils.hasText(properties.getLogin()) && StringUtils.hasText(properties.getSenha());
        if (!temApiKey && !temJwt) {
            log.warn("Integracao notificacao: configure NOTIFICACAO_API_KEY (recomendado) "
                    + "ou NOTIFICACAO_LOGIN/NOTIFICACAO_SENHA");
        } else if (temApiKey) {
            log.info("Integracao notificacao: autenticacao via API Key");
            if (!properties.getApiKey().contains(".")) {
                log.error("NOTIFICACAO_API_KEY incompleta: use a chave inteira (nak_prefixo.segredo), "
                        + "nao apenas o prefixo. Rode scripts/criar-notificacao-api-key.sh para gerar uma nova.");
            }
        }
        if (!temApiKey && properties.getIdOrganizacao() == null) {
            log.warn("Integracao notificacao: id-organizacao padrao nao configurado — "
                    + "organizacoes sem id_organizacao_notificacao nao poderao enviar via JWT");
        }
        if (!StringUtils.hasText(properties.getPublicBaseUrl())) {
            log.warn("Integracao notificacao: public-base-url nao configurada — links no WhatsApp/e-mail ficarao invalidos");
        }
    }
}
