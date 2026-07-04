package com.api_orcafacil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.servico.CategoriaServicoRequest;
import com.api_orcafacil.model.CategoriaServico;
import com.api_orcafacil.service.CategoriaServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias-servico")
public class CategoriaServicoController {

    private final CategoriaServicoService service;

    public CategoriaServicoController(CategoriaServicoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CategoriaServico>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CategoriaServico>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @GetMapping("/sequencia")
    public ResponseEntity<ApiResponseDTO<String>> sequencia() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.sequencia()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<CategoriaServico>> criar(@Valid @RequestBody CategoriaServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Categoria salva", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CategoriaServico>> atualizar(@PathVariable Long id,
            @Valid @RequestBody CategoriaServicoRequest request) {
        request.setIdCategoriaServico(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Categoria atualizada", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Categoria excluida", null));
    }
}
