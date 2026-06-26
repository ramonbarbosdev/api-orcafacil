package com.api_orcafacil.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.OrganizacaoRequestDTO;
import com.api_orcafacil.dto.OrganizacaoResponseDTO;
import com.api_orcafacil.dto.VinculoUsuarioRequestDTO;
import com.api_orcafacil.service.OrganizacaoPlatformService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/organizacoes")
public class OrganizacaoAdminController {

    private final OrganizacaoPlatformService service;

    public OrganizacaoAdminController(OrganizacaoPlatformService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<OrganizacaoResponseDTO>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<OrganizacaoResponseDTO>> criar(
            @Valid @RequestBody OrganizacaoRequestDTO request) {
        return ResponseEntity.status(201)
                .body(new ApiResponseDTO<>("Organizacao criada", service.criar(request)));
    }

    @PostMapping("/{id}/vinculos")
    public ResponseEntity<ApiResponseDTO<Void>> vincularUsuario(
            @PathVariable Long id,
            @Valid @RequestBody VinculoUsuarioRequestDTO request) {
        service.vincularUsuario(id, request);
        return ResponseEntity.ok(new ApiResponseDTO<>("Usuario vinculado", null));
    }
}
