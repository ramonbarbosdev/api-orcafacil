package com.api_orcafacil.notificacao.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.api_orcafacil.notificacao.config.NotificacaoProperties;
import com.api_orcafacil.notificacao.dto.NotificacaoCredenciais;
import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.service.TenantContextService;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;

@Service
@ConditionalOnProperty(name = "app.notificacao.enabled", havingValue = "true")
public class NotificacaoOrganizacaoResolver {

    private final NotificacaoProperties properties;
    private final CentralOrganizacaoRepository organizacaoRepository;
    private final TenantContextService tenantContextService;

    public NotificacaoOrganizacaoResolver(
            NotificacaoProperties properties,
            CentralOrganizacaoRepository organizacaoRepository,
            TenantContextService tenantContextService) {
        this.properties = properties;
        this.organizacaoRepository = organizacaoRepository;
        this.tenantContextService = tenantContextService;
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public Long resolverIdOrganizacaoNotificacao(Long idOrganizacaoOrcafacil) {
        return resolverCredenciais(idOrganizacaoOrcafacil).idOrganizacaoNotificacao();
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public NotificacaoCredenciais resolverCredenciais(Long idOrganizacaoOrcafacil) {
        if (idOrganizacaoOrcafacil == null) {
            return credenciaisGlobais();
        }

        return organizacaoRepository.findById(idOrganizacaoOrcafacil)
                .map(this::credenciaisDaOrganizacao)
                .orElseGet(this::credenciaisGlobais);
    }

    public NotificacaoCredenciais resolverCredenciaisAtual() {
        return resolverCredenciais(tenantContextService.idOrganizacaoObrigatoria());
    }

    private NotificacaoCredenciais credenciaisDaOrganizacao(CentralOrganizacao organizacao) {
        Long idOrg = organizacao.getIdOrganizacaoNotificacao();
        if (idOrg == null || idOrg <= 0) {
            idOrg = properties.getIdOrganizacao();
        }

        String apiKey = organizacao.getDsApiKeyNotificacao();
        if (!StringUtils.hasText(apiKey)) {
            apiKey = properties.getApiKey();
        }

        return new NotificacaoCredenciais(idOrg, apiKey);
    }

    private NotificacaoCredenciais credenciaisGlobais() {
        return new NotificacaoCredenciais(properties.getIdOrganizacao(), properties.getApiKey());
    }
}
