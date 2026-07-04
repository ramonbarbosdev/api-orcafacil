package com.api_orcafacil.notificacao.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NotificacaoProperties.class)
@ConditionalOnProperty(name = "app.notificacao.enabled", havingValue = "true")
public class NotificacaoConfiguration {
}
