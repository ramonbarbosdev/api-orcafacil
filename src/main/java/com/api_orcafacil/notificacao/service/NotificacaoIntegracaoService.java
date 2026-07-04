package com.api_orcafacil.notificacao.service;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;

import com.api_orcafacil.notificacao.client.NotificacaoApiClient;
import com.api_orcafacil.notificacao.dto.NotificacaoCredenciais;
import com.api_orcafacil.notificacao.dto.NotificacaoIntegracaoStatusDTO;
import com.api_orcafacil.notificacao.support.NotificacaoErroParser;
import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.service.TenantContextService;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;

@Service
@ConditionalOnProperty(name = "app.notificacao.enabled", havingValue = "true")
public class NotificacaoIntegracaoService {

    private final NotificacaoApiClient client;
    private final NotificacaoOrganizacaoResolver organizacaoResolver;
    private final TenantContextService tenantContextService;
    private final CentralOrganizacaoRepository organizacaoRepository;

    public NotificacaoIntegracaoService(
            NotificacaoApiClient client,
            NotificacaoOrganizacaoResolver organizacaoResolver,
            TenantContextService tenantContextService,
            CentralOrganizacaoRepository organizacaoRepository) {
        this.client = client;
        this.organizacaoResolver = organizacaoResolver;
        this.tenantContextService = tenantContextService;
        this.organizacaoRepository = organizacaoRepository;
    }

    public NotificacaoIntegracaoStatusDTO verificarIntegracaoAtual() {
        Long idOrgOrcafacil = tenantContextService.idOrganizacaoObrigatoria();
        NotificacaoCredenciais credenciais = organizacaoResolver.resolverCredenciais(idOrgOrcafacil);
        boolean usaApiKeyTenant = usaApiKeyTenant(idOrgOrcafacil);

        if (!credenciais.usaApiKey() && credenciais.idOrganizacaoNotificacao() == null) {
            return statusErro(idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(), usaApiKeyTenant,
                    "Configure API Key ou id_organizacao_notificacao nas configuracoes de integracao.");
        }

        if (credenciais.usaApiKey() && !credenciais.apiKey().contains(".")) {
            return statusErro(idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(), usaApiKeyTenant,
                    "API Key incompleta. Use o formato nak_prefixo.segredo.");
        }

        try {
            if (credenciais.usaApiKey()) {
                Map<String, Object> remoto = client.obterStatusIntegracao(credenciais);
                return montarStatusOk(idOrgOrcafacil, credenciais, usaApiKeyTenant, remoto);
            }
            client.verificarConexao(credenciais);
            return new NotificacaoIntegracaoStatusDTO(
                    true, true, idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(),
                    "Conexao com notificacao-api OK (JWT)",
                    true, false, null, null, usaApiKeyTenant);
        } catch (RestClientResponseException ex) {
            return statusErro(idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(), usaApiKeyTenant,
                    NotificacaoErroParser.extrairMensagem(ex));
        } catch (Exception ex) {
            return statusErro(idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(), usaApiKeyTenant,
                    ex.getMessage());
        }
    }

    private NotificacaoIntegracaoStatusDTO montarStatusOk(
            Long idOrgOrcafacil,
            NotificacaoCredenciais credenciais,
            boolean usaApiKeyTenant,
            Map<String, Object> remoto) {
        boolean whatsappConectado = Boolean.TRUE.equals(remoto.get("whatsappConectado"));
        String whatsappStatus = valorTexto(remoto.get("whatsappStatus"));
        String whatsappTelefone = valorTexto(remoto.get("whatsappTelefone"));
        String whatsappErro = valorTexto(remoto.get("whatsappErro"));

        String mensagem;
        if (whatsappConectado) {
            mensagem = "Integracao OK. WhatsApp conectado"
                    + (StringUtils.hasText(whatsappTelefone) ? " (" + whatsappTelefone + ")." : ".");
        } else if (StringUtils.hasText(whatsappErro)) {
            mensagem = "API Key valida, mas WhatsApp indisponivel: " + whatsappErro;
        } else {
            mensagem = "API Key valida. WhatsApp: " + (whatsappStatus != null ? whatsappStatus : "desconectado");
        }

        return new NotificacaoIntegracaoStatusDTO(
                true,
                true,
                idOrgOrcafacil,
                credenciais.idOrganizacaoNotificacao(),
                mensagem,
                true,
                whatsappConectado,
                whatsappStatus,
                whatsappTelefone,
                usaApiKeyTenant);
    }

    private NotificacaoIntegracaoStatusDTO statusErro(
            Long idOrgOrcafacil,
            Long idOrgNotificacao,
            boolean usaApiKeyTenant,
            String mensagem) {
        return new NotificacaoIntegracaoStatusDTO(
                true, false, idOrgOrcafacil, idOrgNotificacao, mensagem,
                false, false, null, null, usaApiKeyTenant);
    }

    private boolean usaApiKeyTenant(Long idOrgOrcafacil) {
        return organizacaoRepository.findById(idOrgOrcafacil)
                .map(CentralOrganizacao::getDsApiKeyNotificacao)
                .filter(StringUtils::hasText)
                .isPresent();
    }

    private String valorTexto(Object valor) {
        return valor != null ? valor.toString() : null;
    }
}
