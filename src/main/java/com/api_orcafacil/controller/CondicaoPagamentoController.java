package com.api_orcafacil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.condicao.CondicaoPagamentoRequest;
import com.api_orcafacil.model.CondicaoPagamento;
import com.api_orcafacil.service.CondicaoPagamentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/condicoes-pagamento")
public class CondicaoPagamentoController {

    private final CondicaoPagamentoService service;

    public CondicaoPagamentoController(CondicaoPagamentoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CondicaoPagamento>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CondicaoPagamento>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @GetMapping("/sequencia")
    public ResponseEntity<ApiResponseDTO<String>> sequencia() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.sequencia()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<CondicaoPagamento>> criar(@Valid @RequestBody CondicaoPagamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Condicao salva", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CondicaoPagamento>> atualizar(@PathVariable Long id,
            @Valid @RequestBody CondicaoPagamentoRequest request) {
        request.setIdCondicaoPagamento(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Condicao atualizada", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Condicao excluida", null));
    }
}
