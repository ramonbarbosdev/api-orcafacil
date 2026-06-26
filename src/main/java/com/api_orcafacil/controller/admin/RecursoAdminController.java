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
import com.api_orcafacil.dto.ModuloPermissaoAdminDTO;
import com.api_orcafacil.dto.ModuloPermissaoRequestDTO;
import com.api_orcafacil.dto.ModuloPermissaoUpdateDTO;
import com.api_orcafacil.service.PermissaoPlatformService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/recursos")
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class RecursoAdminController {

    private final PermissaoPlatformService service;

    public RecursoAdminController(PermissaoPlatformService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ModuloPermissaoAdminDTO>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarModulos()));
    }

    @GetMapping("/{modulo}")
    public ResponseEntity<ApiResponseDTO<ModuloPermissaoAdminDTO>> buscar(@PathVariable String modulo) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscarModulo(modulo)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ModuloPermissaoAdminDTO>> criar(
            @Valid @RequestBody ModuloPermissaoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Modulo de permissoes criado", service.criarModulo(request)));
    }

    @PutMapping("/{modulo}")
    public ResponseEntity<ApiResponseDTO<ModuloPermissaoAdminDTO>> atualizar(
            @PathVariable String modulo,
            @Valid @RequestBody ModuloPermissaoUpdateDTO request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Modulo atualizado", service.atualizarModulo(modulo, request)));
    }

    @DeleteMapping("/{modulo}")
    public ResponseEntity<ApiResponseDTO<Void>> desativar(@PathVariable String modulo) {
        service.desativarModulo(modulo);
        return ResponseEntity.ok(new ApiResponseDTO<>("Modulo desativado", null));
    }
}
