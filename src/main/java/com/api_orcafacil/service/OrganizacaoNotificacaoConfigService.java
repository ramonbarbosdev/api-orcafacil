package com.api_orcafacil.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoConfigDTO;
import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoConfigRequest;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.notificacao.config.NotificacaoProperties;
import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class OrganizacaoNotificacaoConfigService {

    private final CentralOrganizacaoRepository organizacaoRepository;
    private final TenantContextService tenantContextService;
    private final NotificacaoProperties notificacaoProperties;

    public OrganizacaoNotificacaoConfigService(
            CentralOrganizacaoRepository organizacaoRepository,
            TenantContextService tenantContextService,
            NotificacaoProperties notificacaoProperties) {
        this.organizacaoRepository = organizacaoRepository;
        this.tenantContextService = tenantContextService;
        this.notificacaoProperties = notificacaoProperties;
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public OrganizacaoNotificacaoConfigDTO obterAtual() {
        CentralOrganizacao organizacao = buscarOrganizacaoAtual();
        return paraDto(organizacao);
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public OrganizacaoNotificacaoConfigDTO salvar(OrganizacaoNotificacaoConfigRequest request) {
        CentralOrganizacao organizacao = buscarOrganizacaoAtual();
        if (request.getIdOrganizacaoNotificacao() != null) {
            organizacao.setIdOrganizacaoNotificacao(request.getIdOrganizacaoNotificacao());
        }
        if (StringUtils.hasText(request.getApiKey())) {
            organizacao.setDsApiKeyNotificacao(request.getApiKey().trim());
        }
        return paraDto(organizacaoRepository.save(organizacao));
    }

    private CentralOrganizacao buscarOrganizacaoAtual() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        return organizacaoRepository.findById(idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Organizacao nao encontrada"));
    }

    private OrganizacaoNotificacaoConfigDTO paraDto(CentralOrganizacao organizacao) {
        String apiKeyTenant = organizacao.getDsApiKeyNotificacao();
        boolean usaTenant = StringUtils.hasText(apiKeyTenant);
        boolean usaGlobal = !usaTenant && StringUtils.hasText(notificacaoProperties.getApiKey());
        return new OrganizacaoNotificacaoConfigDTO(
                organizacao.getIdOrganizacao(),
                organizacao.getIdOrganizacaoNotificacao(),
                mascararApiKey(usaTenant ? apiKeyTenant : notificacaoProperties.getApiKey()),
                usaTenant,
                usaGlobal);
    }

    static String mascararApiKey(String apiKey) {
        if (!StringUtils.hasText(apiKey)) {
            return null;
        }
        int dot = apiKey.indexOf('.');
        if (dot <= 0 || dot >= apiKey.length() - 1) {
            return apiKey.length() <= 8 ? "********" : apiKey.substring(0, 4) + "****";
        }
        String prefixo = apiKey.substring(0, dot);
        String segredo = apiKey.substring(dot + 1);
        if (segredo.length() <= 4) {
            return prefixo + ".****";
        }
        return prefixo + "." + segredo.substring(0, 2) + "****" + segredo.substring(segredo.length() - 2);
    }
}
