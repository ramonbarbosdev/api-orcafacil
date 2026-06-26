package com.api_orcafacil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.precificacao.EmpresaMetodoPrecificacaoRequest;
import com.api_orcafacil.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.service.EmpresaMetodoPrecificacaoService;

@RestController
@RequestMapping("/empresa-metodos-precificacao")
public class EmpresaMetodoPrecificacaoController {

    private final EmpresaMetodoPrecificacaoService service;

    public EmpresaMetodoPrecificacaoController(EmpresaMetodoPrecificacaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<EmpresaMetodoPrecificacao>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmpresaMetodoPrecificacao>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<EmpresaMetodoPrecificacao>> criar(@RequestBody EmpresaMetodoPrecificacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Metodo salvo", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmpresaMetodoPrecificacao>> atualizar(@PathVariable Long id,
            @RequestBody EmpresaMetodoPrecificacaoRequest request) {
        request.setIdEmpresaMetodoPrecificacao(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Metodo atualizado", service.salvar(request)));
    }
}
