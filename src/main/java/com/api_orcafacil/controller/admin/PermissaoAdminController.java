package com.api_orcafacil.controller.admin;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.PermissaoModuloDTO;
import com.api_orcafacil.service.PermissaoPlatformService;

@RestController
@RequestMapping("/admin/permissoes")
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class PermissaoAdminController {

    private final PermissaoPlatformService service;

    public PermissaoAdminController(PermissaoPlatformService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PermissaoModuloDTO>>> listarCatalogo() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarCatalogo()));
    }
}
