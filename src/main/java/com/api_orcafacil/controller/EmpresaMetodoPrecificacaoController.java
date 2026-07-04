package com.api_orcafacil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.precificacao.EmpresaMetodoPrecificacaoRequest;
import com.api_orcafacil.dto.precificacao.EmpresaMetodoPrecificacaoResponse;
import com.api_orcafacil.service.EmpresaMetodoPrecificacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/empresa-metodos-precificacao")
public class EmpresaMetodoPrecificacaoController {

    private final EmpresaMetodoPrecificacaoService service;

    public EmpresaMetodoPrecificacaoController(EmpresaMetodoPrecificacaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<EmpresaMetodoPrecificacaoResponse>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmpresaMetodoPrecificacaoResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<EmpresaMetodoPrecificacaoResponse>> criar(
            @Valid @RequestBody EmpresaMetodoPrecificacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Metodo salvo", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmpresaMetodoPrecificacaoResponse>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EmpresaMetodoPrecificacaoRequest request) {
        request.setIdEmpresaMetodoPrecificacao(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Metodo atualizado", service.salvar(request)));
    }
}
