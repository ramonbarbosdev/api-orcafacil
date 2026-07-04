package com.api_orcafacil.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoAdminDTO;
import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoAdminRequest;
import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoTenantDTO;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.notificacao.client.NotificacaoApiClient;
import com.api_orcafacil.notificacao.dto.NotificacaoCredenciais;
import com.api_orcafacil.notificacao.dto.NotificacaoIntegracaoStatusDTO;
import com.api_orcafacil.notificacao.dto.WhatsappSessaoStatusDTO;
import com.api_orcafacil.notificacao.service.NotificacaoIntegracaoService;
import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class OrganizacaoNotificacaoAdminService {

    private final CentralOrganizacaoRepository organizacaoRepository;
    private final TenantContextService tenantContextService;
    private final ObjectProvider<NotificacaoApiClient> notificacaoApiClient;
    private final ObjectProvider<NotificacaoIntegracaoService> integracaoService;

    public OrganizacaoNotificacaoAdminService(
            CentralOrganizacaoRepository organizacaoRepository,
            TenantContextService tenantContextService,
            ObjectProvider<NotificacaoApiClient> notificacaoApiClient,
            ObjectProvider<NotificacaoIntegracaoService> integracaoService) {
        this.organizacaoRepository = organizacaoRepository;
        this.tenantContextService = tenantContextService;
        this.notificacaoApiClient = notificacaoApiClient;
        this.integracaoService = integracaoService;
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public OrganizacaoNotificacaoAdminDTO obter(Long idOrganizacao) {
        return paraAdminDto(buscarOrganizacao(idOrganizacao));
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public OrganizacaoNotificacaoAdminDTO salvar(Long idOrganizacao, OrganizacaoNotificacaoAdminRequest request) {
        CentralOrganizacao organizacao = buscarOrganizacao(idOrganizacao);
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
        if (request.getHabilitada() != null) {
            organizacao.setFlNotificacaoHabilitada(request.getHabilitada());
        }

        CentralOrganizacao salva = organizacaoRepository.save(organizacao);
        sincronizarEmailAlertasNotificacao(salva);
        return paraAdminDto(salva);
    }

    public NotificacaoIntegracaoStatusDTO status(Long idOrganizacao) {
        NotificacaoIntegracaoService service = integracaoService.getIfAvailable();
        if (service == null) {
            return statusDesabilitado();
        }
        return service.verificarIntegracao(idOrganizacao);
    }

    public WhatsappSessaoStatusDTO whatsappStatus(Long idOrganizacao) {
        return exigirCliente().obterWhatsappStatus(credenciaisObrigatorias(idOrganizacao));
    }

    public WhatsappSessaoStatusDTO whatsappConectar(Long idOrganizacao) {
        return exigirCliente().conectarWhatsapp(credenciaisObrigatorias(idOrganizacao));
    }

    public WhatsappSessaoStatusDTO whatsappDesconectar(Long idOrganizacao) {
        return exigirCliente().desconectarWhatsapp(credenciaisObrigatorias(idOrganizacao));
    }

    public WhatsappSessaoStatusDTO whatsappCancelarConexao(Long idOrganizacao) {
        return exigirCliente().cancelarConexaoWhatsapp(credenciaisObrigatorias(idOrganizacao));
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public OrganizacaoNotificacaoTenantDTO obterVisaoTenant() {
        CentralOrganizacao organizacao = buscarOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
        return paraTenantDto(organizacao);
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public OrganizacaoNotificacaoTenantDTO obterVisaoTenant(Long idOrganizacao) {
        return paraTenantDto(buscarOrganizacao(idOrganizacao));
    }

    private CentralOrganizacao buscarOrganizacao(Long idOrganizacao) {
        return organizacaoRepository.findById(idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Organizacao nao encontrada"));
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
            client.atualizarEmailAlertas(
                    credenciaisDaOrganizacao(organizacao),
                    organizacao.getDsEmailAlertasNotificacao());
        } catch (Exception ignored) {
            // sincronizacao best-effort
        }
    }

    private NotificacaoCredenciais credenciaisDaOrganizacao(CentralOrganizacao organizacao) {
        Long idOrgNotificacao = organizacao.getIdOrganizacaoNotificacao();
        if (idOrgNotificacao != null && idOrgNotificacao <= 0) {
            idOrgNotificacao = null;
        }
        return new NotificacaoCredenciais(idOrgNotificacao, organizacao.getDsApiKeyNotificacao());
    }

    private NotificacaoCredenciais credenciaisObrigatorias(Long idOrganizacao) {
        CentralOrganizacao organizacao = buscarOrganizacao(idOrganizacao);
        if (!organizacao.isFlNotificacaoHabilitada()) {
            throw new IllegalStateException("Integracao de notificacoes nao liberada para esta organizacao");
        }
        NotificacaoCredenciais credenciais = credenciaisDaOrganizacao(organizacao);
        if (!credenciais.usaApiKey()) {
            throw new IllegalStateException("API Key da notificacao-api nao configurada para esta organizacao");
        }
        return credenciais;
    }

    private NotificacaoApiClient exigirCliente() {
        NotificacaoApiClient client = notificacaoApiClient.getIfAvailable();
        if (client == null) {
            throw new IllegalStateException("Integracao de notificacoes desabilitada");
        }
        return client;
    }

    private OrganizacaoNotificacaoAdminDTO paraAdminDto(CentralOrganizacao organizacao) {
        String apiKey = organizacao.getDsApiKeyNotificacao();
        return new OrganizacaoNotificacaoAdminDTO(
                organizacao.getIdOrganizacao(),
                organizacao.getIdOrganizacaoNotificacao(),
                apiKey,
                StringUtils.hasText(apiKey),
                organizacao.isFlNotificacaoHabilitada(),
                organizacao.getDsEmailAlertasNotificacao());
    }

    private OrganizacaoNotificacaoTenantDTO paraTenantDto(CentralOrganizacao organizacao) {
        boolean configurada = StringUtils.hasText(organizacao.getDsApiKeyNotificacao());
        boolean habilitada = organizacao.isFlNotificacaoHabilitada() && configurada;
        String mensagem;
        if (!organizacao.isFlNotificacaoHabilitada()) {
            mensagem = "Integracao de notificacoes nao liberada para esta organizacao. Contate o suporte.";
        } else if (!configurada) {
            mensagem = "Integracao em configuracao pelo administrador da plataforma.";
        } else {
            mensagem = "Integracao ativa. Conecte o WhatsApp abaixo para enviar orcamentos.";
        }
        return new OrganizacaoNotificacaoTenantDTO(habilitada, configurada, mensagem);
    }

    private NotificacaoIntegracaoStatusDTO statusDesabilitado() {
        return new NotificacaoIntegracaoStatusDTO(
                false, false, null, null,
                "Configure NOTIFICACAO_ENABLED=true para habilitar",
                false, false, null, null, false);
    }
}
