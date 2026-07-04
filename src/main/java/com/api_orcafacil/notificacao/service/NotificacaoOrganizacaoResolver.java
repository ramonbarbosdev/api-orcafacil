package com.api_orcafacil.notificacao.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.api_orcafacil.notificacao.dto.NotificacaoCredenciais;
import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.service.TenantContextService;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;

@Service
@ConditionalOnProperty(name = "app.notificacao.enabled", havingValue = "true")
public class NotificacaoOrganizacaoResolver {

    public static final String MSG_INTEGRACAO_NAO_CONFIGURADA =
            "Integracao de notificacoes nao configurada. Contate o administrador da plataforma.";

    private final CentralOrganizacaoRepository organizacaoRepository;
    private final TenantContextService tenantContextService;

    public NotificacaoOrganizacaoResolver(
            CentralOrganizacaoRepository organizacaoRepository,
            TenantContextService tenantContextService) {
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
            return semCredenciais();
        }

        return organizacaoRepository.findById(idOrganizacaoOrcafacil)
                .map(this::credenciaisDaOrganizacao)
                .orElseGet(this::semCredenciais);
    }

    public NotificacaoCredenciais resolverCredenciaisAtual() {
        return resolverCredenciais(tenantContextService.idOrganizacaoObrigatoria());
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public boolean integracaoConfigurada(Long idOrganizacaoOrcafacil) {
        if (idOrganizacaoOrcafacil == null) {
            return false;
        }
        return organizacaoRepository.findById(idOrganizacaoOrcafacil)
                .map(org -> org.isFlNotificacaoHabilitada() && resolverCredenciais(idOrganizacaoOrcafacil).usaApiKey())
                .orElse(false);
    }

    private NotificacaoCredenciais credenciaisDaOrganizacao(CentralOrganizacao organizacao) {
        String apiKey = organizacao.getDsApiKeyNotificacao();
        Long idOrgNotificacao = organizacao.getIdOrganizacaoNotificacao();
        if (idOrgNotificacao != null && idOrgNotificacao <= 0) {
            idOrgNotificacao = null;
        }
        return new NotificacaoCredenciais(idOrgNotificacao, apiKey);
    }

    private NotificacaoCredenciais semCredenciais() {
        return new NotificacaoCredenciais(null, null);
    }
}
