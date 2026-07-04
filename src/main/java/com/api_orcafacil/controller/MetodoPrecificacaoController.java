package com.api_orcafacil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.precificacao.MetodoPrecificacaoRequest;
import com.api_orcafacil.dto.precificacao.MetodoPrecificacaoResponse;
import com.api_orcafacil.service.MetodoPrecificacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/metodos-precificacao")
public class MetodoPrecificacaoController {

    private final MetodoPrecificacaoService service;

    public MetodoPrecificacaoController(MetodoPrecificacaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<MetodoPrecificacaoResponse>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MetodoPrecificacaoResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @GetMapping("/tipos")
    public ResponseEntity<ApiResponseDTO<TipoPrecificacao[]>> tipos() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", TipoPrecificacao.values()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<MetodoPrecificacaoResponse>> criar(
            @Valid @RequestBody MetodoPrecificacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Metodo salvo", service.salvar(request)));
    }
}
