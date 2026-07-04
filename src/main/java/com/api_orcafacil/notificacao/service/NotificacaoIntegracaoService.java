package com.api_orcafacil.notificacao.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import com.api_orcafacil.notificacao.client.NotificacaoApiClient;
import com.api_orcafacil.notificacao.dto.NotificacaoCredenciais;
import com.api_orcafacil.notificacao.dto.NotificacaoIntegracaoStatusDTO;
import com.api_orcafacil.notificacao.support.NotificacaoErroParser;
import com.api_orcafacil.service.TenantContextService;

@Service
@ConditionalOnProperty(name = "app.notificacao.enabled", havingValue = "true")
public class NotificacaoIntegracaoService {

    private final NotificacaoApiClient client;
    private final NotificacaoOrganizacaoResolver organizacaoResolver;
    private final TenantContextService tenantContextService;

    public NotificacaoIntegracaoService(
            NotificacaoApiClient client,
            NotificacaoOrganizacaoResolver organizacaoResolver,
            TenantContextService tenantContextService) {
        this.client = client;
        this.organizacaoResolver = organizacaoResolver;
        this.tenantContextService = tenantContextService;
    }

    public NotificacaoIntegracaoStatusDTO verificarIntegracaoAtual() {
        Long idOrgOrcafacil = tenantContextService.idOrganizacaoObrigatoria();
        NotificacaoCredenciais credenciais = organizacaoResolver.resolverCredenciais(idOrgOrcafacil);

        if (!credenciais.usaApiKey() && credenciais.idOrganizacaoNotificacao() == null) {
            return new NotificacaoIntegracaoStatusDTO(
                    true, false, idOrgOrcafacil, null,
                    "Configure NOTIFICACAO_API_KEY ou id_organizacao_notificacao + credenciais JWT.");
        }

        try {
            client.verificarConexao(credenciais);
            String modo = credenciais.usaApiKey() ? "API Key" : "JWT";
            return new NotificacaoIntegracaoStatusDTO(
                    true, true, idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(),
                    "Conexao com notificacao-api OK (" + modo + ")");
        } catch (RestClientResponseException ex) {
            return new NotificacaoIntegracaoStatusDTO(
                    true, false, idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(),
                    NotificacaoErroParser.extrairMensagem(ex));
        } catch (Exception ex) {
            return new NotificacaoIntegracaoStatusDTO(
                    true, false, idOrgOrcafacil, credenciais.idOrganizacaoNotificacao(), ex.getMessage());
        }
    }
}
