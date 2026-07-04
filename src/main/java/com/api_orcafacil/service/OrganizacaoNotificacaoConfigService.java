package com.api_orcafacil.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoConfigDTO;
import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoConfigRequest;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.notificacao.client.NotificacaoApiClient;
import com.api_orcafacil.notificacao.dto.NotificacaoCredenciais;
import com.api_orcafacil.notificacao.dto.NotificacaoCredenciais;
import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class OrganizacaoNotificacaoConfigService {

    private final CentralOrganizacaoRepository organizacaoRepository;
    private final TenantContextService tenantContextService;
    private final ObjectProvider<NotificacaoApiClient> notificacaoApiClient;

    public OrganizacaoNotificacaoConfigService(
            CentralOrganizacaoRepository organizacaoRepository,
            TenantContextService tenantContextService,
            ObjectProvider<NotificacaoApiClient> notificacaoApiClient) {
        this.organizacaoRepository = organizacaoRepository;
        this.tenantContextService = tenantContextService;
        this.notificacaoApiClient = notificacaoApiClient;
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
        if (request.getEmailAlertas() != null) {
            String email = request.getEmailAlertas().trim();
            organizacao.setDsEmailAlertasNotificacao(StringUtils.hasText(email) ? email : null);
        }

        CentralOrganizacao salva = organizacaoRepository.save(organizacao);
        sincronizarEmailAlertasNotificacao(salva);
        return paraDto(salva);
    }

    private void sincronizarEmailAlertasNotificacao(CentralOrganizacao organizacao) {
        String apiKey = organizacao.getDsApiKeyNotificacao();
        if (!StringUtils.hasText(apiKey)) {
            return;
        }
        try {
            NotificacaoApiClient client = notificacaoApiClient.getIfAvailable();
            if (client == null) {
                return;
            }
            NotificacaoCredenciais credenciais = new NotificacaoCredenciais(
                    organizacao.getIdOrganizacaoNotificacao(),
                    apiKey);
            client.atualizarEmailAlertas(
                    credenciais,
                    organizacao.getDsEmailAlertasNotificacao());
        } catch (Exception ignored) {
            // sincronizacao best-effort; admin pode configurar no painel de notificacoes
        }
    }

    private CentralOrganizacao buscarOrganizacaoAtual() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        return organizacaoRepository.findById(idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Organizacao nao encontrada"));
    }

    private OrganizacaoNotificacaoConfigDTO paraDto(CentralOrganizacao organizacao) {
        String apiKey = organizacao.getDsApiKeyNotificacao();
        return new OrganizacaoNotificacaoConfigDTO(
                organizacao.getIdOrganizacao(),
                organizacao.getIdOrganizacaoNotificacao(),
                apiKey,
                StringUtils.hasText(apiKey),
                organizacao.getDsEmailAlertasNotificacao());
    }
}
