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
import com.api_orcafacil.dto.PermissaoModuloDTO;
import com.api_orcafacil.dto.permissao.CatalogoRecursoItemDTO;
import com.api_orcafacil.dto.permissao.PermissaoDetalheDTO;
import com.api_orcafacil.dto.permissao.PermissaoItemRequestDTO;
import com.api_orcafacil.dto.permissao.PermissaoItemUpdateDTO;
import com.api_orcafacil.dto.permissao.RegistrarRecursoRequestDTO;
import com.api_orcafacil.dto.permissao.RegistrarRecursoResponseDTO;
import com.api_orcafacil.service.PermissaoPlatformService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/permissoes")
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class PermissaoAdminController {

    private final PermissaoPlatformService service;

    public PermissaoAdminController(PermissaoPlatformService service) {
        this.service = service;
    }

    /** Catálogo agrupado para matriz de papéis/planos (legado). */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PermissaoModuloDTO>>> listarCatalogo() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarCatalogo()));
    }

    /** União catálogo curado + recursos descobertos automaticamente. */
    @GetMapping("/catalogo")
    public ResponseEntity<ApiResponseDTO<List<CatalogoRecursoItemDTO>>> listarCatalogoRecursos() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarCatalogoRecursos()));
    }

    /** Recursos pendentes ou fora do catálogo curado. */
    @GetMapping("/catalogo/sugeridos")
    public ResponseEntity<ApiResponseDTO<List<CatalogoRecursoItemDTO>>> listarCatalogoSugeridos() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarCatalogoSugeridos()));
    }

    /** Cadastro idempotente em lote por recurso/módulo. */
    @PostMapping("/registrar-recurso")
    public ResponseEntity<ApiResponseDTO<RegistrarRecursoResponseDTO>> registrarRecurso(
            @Valid @RequestBody RegistrarRecursoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Permissoes registradas", service.registrarRecurso(request)));
    }

    /** CRUD manual — listagem plana. */
    @GetMapping("/itens")
    public ResponseEntity<ApiResponseDTO<List<PermissaoDetalheDTO>>> listarItens() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarPermissoesDetalhadas()));
    }

    @PostMapping("/itens")
    public ResponseEntity<ApiResponseDTO<PermissaoDetalheDTO>> criarItem(
            @Valid @RequestBody PermissaoItemRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Permissao criada", service.criarPermissao(request)));
    }

    @PutMapping("/itens/{id}")
    public ResponseEntity<ApiResponseDTO<PermissaoDetalheDTO>> atualizarItem(
            @PathVariable Long id,
            @Valid @RequestBody PermissaoItemUpdateDTO request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Permissao atualizada", service.atualizarPermissao(id, request)));
    }

    @DeleteMapping("/itens/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> desativarItem(@PathVariable Long id) {
        service.desativarPermissao(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Permissao desativada", null));
    }
}
