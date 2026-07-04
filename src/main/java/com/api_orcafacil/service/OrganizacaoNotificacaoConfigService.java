package com.api_orcafacil.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoTenantDTO;
import com.api_orcafacil.notificacao.dto.WhatsappSessaoStatusDTO;
import com.api_orcafacil.notificacao.support.NotificacaoErroParser;

import org.springframework.web.client.RestClientResponseException;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class OrganizacaoNotificacaoConfigService {

    private final ObjectProvider<OrganizacaoNotificacaoAdminService> adminService;
    private final TenantContextService tenantContextService;

    public OrganizacaoNotificacaoConfigService(
            ObjectProvider<OrganizacaoNotificacaoAdminService> adminService,
            TenantContextService tenantContextService) {
        this.adminService = adminService;
        this.tenantContextService = tenantContextService;
    }

    public OrganizacaoNotificacaoTenantDTO obterAtual() {
        OrganizacaoNotificacaoAdminService service = adminService.getIfAvailable();
        if (service == null) {
            return new OrganizacaoNotificacaoTenantDTO(false, false, "Central SaaS desabilitada");
        }
        return service.obterVisaoTenant();
    }

    public WhatsappSessaoStatusDTO whatsappStatus() {
        return executarWhatsapp(() -> exigirAdmin().whatsappStatus(idOrganizacaoAtual()));
    }

    public WhatsappSessaoStatusDTO whatsappConectar() {
        return executarWhatsapp(() -> exigirAdmin().whatsappConectar(idOrganizacaoAtual()));
    }

    public WhatsappSessaoStatusDTO whatsappDesconectar() {
        return executarWhatsapp(() -> exigirAdmin().whatsappDesconectar(idOrganizacaoAtual()));
    }

    public WhatsappSessaoStatusDTO whatsappCancelarConexao() {
        return executarWhatsapp(() -> exigirAdmin().whatsappCancelarConexao(idOrganizacaoAtual()));
    }

    private WhatsappSessaoStatusDTO executarWhatsapp(java.util.function.Supplier<WhatsappSessaoStatusDTO> acao) {
        try {
            WhatsappSessaoStatusDTO resposta = acao.get();
            if (resposta != null && resposta.getErro() != null && !resposta.getErro().isBlank()) {
                resposta.setSucesso(false);
            }
            return resposta;
        } catch (RestClientResponseException ex) {
            return erroWhatsapp(NotificacaoErroParser.interpretar(ex).mensagemUsuario());
        } catch (Exception ex) {
            return erroWhatsapp(NotificacaoErroParser.interpretarGenerico(ex).mensagemUsuario());
        }
    }

    private WhatsappSessaoStatusDTO erroWhatsapp(String mensagem) {
        WhatsappSessaoStatusDTO dto = new WhatsappSessaoStatusDTO();
        dto.setSucesso(false);
        dto.setStatus("ERRO");
        dto.setConectado(false);
        dto.setErro(mensagem);
        return dto;
    }

    private Long idOrganizacaoAtual() {
        return tenantContextService.idOrganizacaoObrigatoria();
    }

    private OrganizacaoNotificacaoAdminService exigirAdmin() {
        OrganizacaoNotificacaoAdminService service = adminService.getIfAvailable();
        if (service == null) {
            throw new IllegalStateException("Central SaaS desabilitada");
        }
        return service;
    }
}
