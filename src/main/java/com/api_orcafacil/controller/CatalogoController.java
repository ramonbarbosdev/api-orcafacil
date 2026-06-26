package com.api_orcafacil.controller;

import java.util.List;

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

import com.api_orcafacil.common.TipoItem;
import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.catalogo.CatalogoRequest;
import com.api_orcafacil.dto.catalogo.CatalogoResponse;
import com.api_orcafacil.service.CatalogoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/catalogos")
public class CatalogoController {

    private final CatalogoService service;

    public CatalogoController(CatalogoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CatalogoResponse>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/sequencia")
    public ResponseEntity<ApiResponseDTO<String>> sequencia() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.sequencia()));
    }

    @GetMapping({ "/tipo-item", "/tipo-item/" })
    public ResponseEntity<ApiResponseDTO<TipoItem[]>> listarTiposItem() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", TipoItem.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CatalogoResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<CatalogoResponse>> criar(@Valid @RequestBody CatalogoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Catalogo salvo", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CatalogoResponse>> atualizar(@PathVariable Long id,
            @Valid @RequestBody CatalogoRequest request) {
        request.setIdCatalogo(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Catalogo atualizado", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Catalogo excluido", null));
    }
}
