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
        return verificarIntegracao(tenantContextService.idOrganizacaoObrigatoria());
    }

    public NotificacaoIntegracaoStatusDTO verificarIntegracao(Long idOrgOrcafacil) {
        CentralOrganizacao organizacao = organizacaoRepository.findById(idOrgOrcafacil).orElse(null);
        if (organizacao == null) {
            return statusErro(idOrgOrcafacil, null, "Organizacao nao encontrada");
        }
        if (!organizacao.isFlNotificacaoHabilitada()) {
            return statusErro(idOrgOrcafacil, organizacao.getIdOrganizacaoNotificacao(),
                    "Integracao de notificacoes nao liberada para esta organizacao.");
        }

        NotificacaoCredenciais credenciais = organizacaoResolver.resolverCredenciais(idOrgOrcafacil);
        if (!credenciais.usaApiKey()) {
            return statusErro(idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(),
                    NotificacaoOrganizacaoResolver.MSG_INTEGRACAO_NAO_CONFIGURADA);
        }

        if (!credenciais.apiKey().contains(".")) {
            return statusErro(idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(),
                    "API Key incompleta. Use o formato nak_prefixo.segredo.");
        }

        try {
            Map<String, Object> remoto = client.obterStatusIntegracao(credenciais);
            return montarStatusOk(idOrgOrcafacil, credenciais, remoto);
        } catch (RestClientResponseException ex) {
            return statusErro(idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(),
                    NotificacaoErroParser.extrairMensagem(ex));
        } catch (Exception ex) {
            return statusErro(idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(), ex.getMessage());
        }
    }

    private NotificacaoIntegracaoStatusDTO montarStatusOk(
            Long idOrgOrcafacil,
            NotificacaoCredenciais credenciais,
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
                true);
    }

    private NotificacaoIntegracaoStatusDTO statusErro(
            Long idOrgOrcafacil,
            Long idOrgNotificacao,
            String mensagem) {
        return new NotificacaoIntegracaoStatusDTO(
                true, false, idOrgOrcafacil, idOrgNotificacao, mensagem,
                false, false, null, null, false);
    }

    private String valorTexto(Object valor) {
        return valor != null ? valor.toString() : null;
    }
}
