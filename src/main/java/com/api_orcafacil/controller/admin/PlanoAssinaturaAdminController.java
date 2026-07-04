package com.api_orcafacil.controller.admin;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.PermissoesUpdateDTO;
import com.api_orcafacil.dto.plano.PlanoLimiteItemDTO;
import com.api_orcafacil.dto.plano.PlanoLimitesUpdateDTO;
import com.api_orcafacil.dto.plano.TipoLimiteResponseDTO;
import com.api_orcafacil.dto.precificacao.PlanoAssinaturaRequest;
import com.api_orcafacil.dto.precificacao.PlanoAssinaturaResponse;
import com.api_orcafacil.service.PlanoAssinaturaPlatformService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/planos-assinatura")
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class PlanoAssinaturaAdminController {

    private final PlanoAssinaturaPlatformService service;

    public PlanoAssinaturaAdminController(PlanoAssinaturaPlatformService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PlanoAssinaturaResponse>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PlanoAssinaturaResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<PlanoAssinaturaResponse>> criar(@Valid @RequestBody PlanoAssinaturaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Plano criado", service.criar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PlanoAssinaturaResponse>> atualizar(@PathVariable Long id,
            @Valid @RequestBody PlanoAssinaturaRequest request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Plano atualizado", service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Plano excluido", null));
    }

    @GetMapping("/{id}/permissoes")
    public ResponseEntity<ApiResponseDTO<List<String>>> listarPermissoes(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarPermissoes(id)));
    }

    @PutMapping("/{id}/permissoes")
    public ResponseEntity<ApiResponseDTO<List<String>>> atualizarPermissoes(
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody PermissoesUpdateDTO request) {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Permissoes do plano atualizadas",
                service.atualizarPermissoes(id, request.chaves())));
    }

    @GetMapping("/tipos-limite")
    public ResponseEntity<ApiResponseDTO<List<TipoLimiteResponseDTO>>> listarTiposLimite() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarTiposLimite()));
    }

    @GetMapping("/{id}/limites")
    public ResponseEntity<ApiResponseDTO<List<PlanoLimiteItemDTO>>> listarLimites(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarLimites(id)));
    }

    @PutMapping("/{id}/limites")
    public ResponseEntity<ApiResponseDTO<List<PlanoLimiteItemDTO>>> atualizarLimites(
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody PlanoLimitesUpdateDTO request) {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Limites do plano atualizados",
                service.atualizarLimites(id, request)));
    }
}
