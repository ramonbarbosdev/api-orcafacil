package com.api_orcafacil.controller;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.notificacao.dto.NotificacaoIntegracaoStatusDTO;
import com.api_orcafacil.notificacao.service.NotificacaoIntegracaoService;

@RestController
@RequestMapping("/integracao-notificacao")
public class IntegracaoNotificacaoController {

    private final ObjectProvider<NotificacaoIntegracaoService> integracaoService;

    public IntegracaoNotificacaoController(ObjectProvider<NotificacaoIntegracaoService> integracaoService) {
        this.integracaoService = integracaoService;
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponseDTO<NotificacaoIntegracaoStatusDTO>> status() {
        NotificacaoIntegracaoService service = integracaoService.getIfAvailable();
        if (service == null) {
            return ResponseEntity.ok(new ApiResponseDTO<>("Integracao desabilitada",
                    new NotificacaoIntegracaoStatusDTO(false, false, null, null,
                            "Configure NOTIFICACAO_ENABLED=true para habilitar")));
        }
        return ResponseEntity.ok(new ApiResponseDTO<>("Status da integracao", service.verificarIntegracaoAtual()));
    }
}
