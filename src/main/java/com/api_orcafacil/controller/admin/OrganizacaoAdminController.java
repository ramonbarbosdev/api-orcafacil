package com.api_orcafacil.controller.admin;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.OrganizacaoRequestDTO;
import com.api_orcafacil.dto.OrganizacaoResponseDTO;
import com.api_orcafacil.dto.VinculoUsuarioRequestDTO;
import com.api_orcafacil.dto.VinculoUsuarioResponseDTO;
import com.api_orcafacil.dto.VinculoUsuarioUpdateDTO;
import com.api_orcafacil.service.OrganizacaoPlatformService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/organizacoes")
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class OrganizacaoAdminController {

    private final OrganizacaoPlatformService service;

    public OrganizacaoAdminController(OrganizacaoPlatformService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<OrganizacaoResponseDTO>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<OrganizacaoResponseDTO>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<OrganizacaoResponseDTO>> criar(
            @Valid @RequestBody OrganizacaoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Organizacao criada", service.criar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<OrganizacaoResponseDTO>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrganizacaoRequestDTO request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Organizacao atualizada", service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Organizacao excluida", null));
    }

    @GetMapping("/{id}/vinculos")
    public ResponseEntity<ApiResponseDTO<List<VinculoUsuarioResponseDTO>>> listarVinculos(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarVinculos(id)));
    }

    @PostMapping("/{id}/vinculos")
    public ResponseEntity<ApiResponseDTO<Void>> vincularUsuario(
            @PathVariable Long id,
            @Valid @RequestBody VinculoUsuarioRequestDTO request) {
        service.vincularUsuario(id, request);
        return ResponseEntity.ok(new ApiResponseDTO<>("Usuario vinculado", null));
    }

    @PutMapping("/{id}/vinculos/{idUsuario}")
    public ResponseEntity<ApiResponseDTO<Void>> atualizarVinculo(
            @PathVariable Long id,
            @PathVariable Long idUsuario,
            @Valid @RequestBody VinculoUsuarioUpdateDTO request) {
        service.atualizarVinculo(id, idUsuario, request);
        return ResponseEntity.ok(new ApiResponseDTO<>("Vinculo atualizado", null));
    }

    @DeleteMapping("/{id}/vinculos/{idUsuario}")
    public ResponseEntity<ApiResponseDTO<Void>> excluirVinculo(
            @PathVariable Long id,
            @PathVariable Long idUsuario) {
        service.excluirVinculo(id, idUsuario);
        return ResponseEntity.ok(new ApiResponseDTO<>("Vinculo removido", null));
    }
}
