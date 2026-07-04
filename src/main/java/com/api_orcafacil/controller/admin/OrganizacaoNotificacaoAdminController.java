package com.api_orcafacil.controller.admin;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoAdminDTO;
import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoAdminRequest;
import com.api_orcafacil.notificacao.dto.NotificacaoIntegracaoStatusDTO;
import com.api_orcafacil.service.OrganizacaoNotificacaoAdminService;

@RestController
@RequestMapping("/admin/organizacoes/{idOrganizacao}/notificacao")
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class OrganizacaoNotificacaoAdminController {

    private final ObjectProvider<OrganizacaoNotificacaoAdminService> service;

    public OrganizacaoNotificacaoAdminController(ObjectProvider<OrganizacaoNotificacaoAdminService> service) {
        this.service = service;
    }

    @GetMapping("/config")
    public ResponseEntity<ApiResponseDTO<OrganizacaoNotificacaoAdminDTO>> obterConfig(
            @PathVariable Long idOrganizacao) {
        OrganizacaoNotificacaoAdminService svc = exigirService();
        return ResponseEntity.ok(new ApiResponseDTO<>("Configuracao de notificacao", svc.obter(idOrganizacao)));
    }

    @PutMapping("/config")
    public ResponseEntity<ApiResponseDTO<OrganizacaoNotificacaoAdminDTO>> salvarConfig(
            @PathVariable Long idOrganizacao,
            @RequestBody OrganizacaoNotificacaoAdminRequest request) {
        OrganizacaoNotificacaoAdminService svc = exigirService();
        return ResponseEntity.ok(new ApiResponseDTO<>("Configuracao salva", svc.salvar(idOrganizacao, request)));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponseDTO<NotificacaoIntegracaoStatusDTO>> status(
            @PathVariable Long idOrganizacao) {
        OrganizacaoNotificacaoAdminService svc = exigirService();
        return ResponseEntity.ok(new ApiResponseDTO<>("Status da integracao", svc.status(idOrganizacao)));
    }

    private OrganizacaoNotificacaoAdminService exigirService() {
        OrganizacaoNotificacaoAdminService svc = service.getIfAvailable();
        if (svc == null) {
            throw new IllegalStateException("Central SaaS desabilitada");
        }
        return svc;
    }
}
