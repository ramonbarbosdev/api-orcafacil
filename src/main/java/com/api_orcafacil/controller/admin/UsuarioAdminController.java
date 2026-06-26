package com.api_orcafacil.controller.admin;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.UsuarioBuscaDTO;
import com.api_orcafacil.service.UsuarioPlatformService;

@RestController
@RequestMapping("/admin/usuarios")
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class UsuarioAdminController {

    private final UsuarioPlatformService service;

    public UsuarioAdminController(UsuarioPlatformService service) {
        this.service = service;
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<UsuarioBuscaDTO>> buscarPorCpf(@RequestParam String nuCpf) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscarPorCpf(nuCpf)));
    }
}
