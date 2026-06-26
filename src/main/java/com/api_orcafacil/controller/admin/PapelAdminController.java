package com.api_orcafacil.controller.admin;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.PapelDetalheDTO;
import com.api_orcafacil.dto.PapelResponseDTO;
import com.api_orcafacil.dto.PermissoesUpdateDTO;
import com.api_orcafacil.service.PapelPlatformService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/papeis")
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class PapelAdminController {

    private final PapelPlatformService service;

    public PapelAdminController(PapelPlatformService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PapelResponseDTO>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PapelDetalheDTO>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @PutMapping("/{id}/permissoes")
    public ResponseEntity<ApiResponseDTO<PapelDetalheDTO>> atualizarPermissoes(
            @PathVariable Long id,
            @Valid @RequestBody PermissoesUpdateDTO request) {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Permissoes do papel atualizadas",
                service.atualizarPermissoes(id, request.chaves())));
    }
}
