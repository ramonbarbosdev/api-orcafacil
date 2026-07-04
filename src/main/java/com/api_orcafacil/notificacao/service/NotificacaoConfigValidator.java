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
            log.warn("Integracao notificacao: NOTIFICACAO_BASE_URL nao configurada");
        }
        log.info("Integracao notificacao: API Key por organizacao em Configuracoes > Integracao WhatsApp");
        if (!StringUtils.hasText(properties.getPublicBaseUrl())) {
            log.warn("Integracao notificacao: NOTIFICACAO_PUBLIC_BASE_URL nao configurada — links no WhatsApp ficarao invalidos");
        }
    }
}
