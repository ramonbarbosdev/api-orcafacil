package com.api_orcafacil.controller;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoTenantDTO;
import com.api_orcafacil.notificacao.dto.NotificacaoIntegracaoStatusDTO;
import com.api_orcafacil.notificacao.dto.WhatsappSessaoStatusDTO;
import com.api_orcafacil.notificacao.service.NotificacaoIntegracaoService;
import com.api_orcafacil.service.OrganizacaoNotificacaoConfigService;

@RestController
@RequestMapping("/integracao-notificacao")
public class IntegracaoNotificacaoController {

    private final ObjectProvider<NotificacaoIntegracaoService> integracaoService;
    private final ObjectProvider<OrganizacaoNotificacaoConfigService> configService;

    public IntegracaoNotificacaoController(
            ObjectProvider<NotificacaoIntegracaoService> integracaoService,
            ObjectProvider<OrganizacaoNotificacaoConfigService> configService) {
        this.integracaoService = integracaoService;
        this.configService = configService;
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponseDTO<NotificacaoIntegracaoStatusDTO>> status() {
        NotificacaoIntegracaoService service = integracaoService.getIfAvailable();
        if (service == null) {
            return ResponseEntity.ok(new ApiResponseDTO<>("Integracao desabilitada",
                    new NotificacaoIntegracaoStatusDTO(
                            false, false, null, null,
                            "Configure NOTIFICACAO_ENABLED=true para habilitar",
                            false, false, null, null, false)));
        }
        return ResponseEntity.ok(new ApiResponseDTO<>("Status da integracao", service.verificarIntegracaoAtual()));
    }

    @GetMapping("/config")
    public ResponseEntity<ApiResponseDTO<OrganizacaoNotificacaoTenantDTO>> obterConfig() {
        OrganizacaoNotificacaoConfigService service = configService.getIfAvailable();
        if (service == null) {
            return ResponseEntity.ok(new ApiResponseDTO<>("Central desabilitada",
                    new OrganizacaoNotificacaoTenantDTO(false, false, "Central SaaS desabilitada")));
        }
        return ResponseEntity.ok(new ApiResponseDTO<>("Integracao de notificacoes", service.obterAtual()));
    }

    @GetMapping("/whatsapp/status")
    public ResponseEntity<ApiResponseDTO<WhatsappSessaoStatusDTO>> whatsappStatus() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Status WhatsApp", exigirConfig().whatsappStatus()));
    }

    @PostMapping("/whatsapp/conectar")
    public ResponseEntity<ApiResponseDTO<WhatsappSessaoStatusDTO>> whatsappConectar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Conexao iniciada", exigirConfig().whatsappConectar()));
    }

    @PostMapping("/whatsapp/desconectar")
    public ResponseEntity<ApiResponseDTO<WhatsappSessaoStatusDTO>> whatsappDesconectar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("WhatsApp desconectado", exigirConfig().whatsappDesconectar()));
    }

    @PostMapping("/whatsapp/cancelar-conexao")
    public ResponseEntity<ApiResponseDTO<WhatsappSessaoStatusDTO>> whatsappCancelarConexao() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Conexao cancelada", exigirConfig().whatsappCancelarConexao()));
    }

    private OrganizacaoNotificacaoConfigService exigirConfig() {
        OrganizacaoNotificacaoConfigService service = configService.getIfAvailable();
        if (service == null) {
            throw new IllegalStateException("Central SaaS desabilitada");
        }
        return service;
    }
}
