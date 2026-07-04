package com.api_orcafacil.controller;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoConfigDTO;
import com.api_orcafacil.dto.integracao.OrganizacaoNotificacaoConfigRequest;
import com.api_orcafacil.notificacao.dto.NotificacaoIntegracaoStatusDTO;
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
    public ResponseEntity<ApiResponseDTO<OrganizacaoNotificacaoConfigDTO>> obterConfig() {
        OrganizacaoNotificacaoConfigService service = configService.getIfAvailable();
        if (service == null) {
            return ResponseEntity.ok(new ApiResponseDTO<>("Central desabilitada", null));
        }
        return ResponseEntity.ok(new ApiResponseDTO<>("Configuracao da integracao", service.obterAtual()));
    }

    @PutMapping("/config")
    public ResponseEntity<ApiResponseDTO<OrganizacaoNotificacaoConfigDTO>> salvarConfig(
            @RequestBody OrganizacaoNotificacaoConfigRequest request) {
        OrganizacaoNotificacaoConfigService service = configService.getIfAvailable();
        if (service == null) {
            return ResponseEntity.ok(new ApiResponseDTO<>("Central desabilitada", null));
        }
        return ResponseEntity.ok(new ApiResponseDTO<>("Configuracao salva", service.salvar(request)));
    }
}
