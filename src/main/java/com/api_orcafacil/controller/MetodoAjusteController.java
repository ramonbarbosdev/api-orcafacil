package com.api_orcafacil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api_orcafacil.common.TipoAjuste;
import com.api_orcafacil.common.TipoOperacaoAjuste;
import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.precificacao.MetodoAjusteRequest;
import com.api_orcafacil.model.MetodoAjuste;
import com.api_orcafacil.service.MetodoAjusteService;

@RestController
@RequestMapping("/metodos-ajuste")
public class MetodoAjusteController {

    private final MetodoAjusteService service;

    public MetodoAjusteController(MetodoAjusteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<MetodoAjuste>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping({ "/tipo-ajuste", "/tipo-ajuste/" })
    public ResponseEntity<ApiResponseDTO<TipoAjuste[]>> listarTiposAjuste() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", TipoAjuste.values()));
    }

    @GetMapping({ "/tipo-operacao", "/tipo-operacao/" })
    public ResponseEntity<ApiResponseDTO<TipoOperacaoAjuste[]>> listarTiposOperacao() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", TipoOperacaoAjuste.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MetodoAjuste>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<MetodoAjuste>> criar(@RequestBody MetodoAjusteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Ajuste salvo", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MetodoAjuste>> atualizar(@PathVariable Long id,
            @RequestBody MetodoAjusteRequest request) {
        request.setIdMetodoAjuste(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Ajuste atualizado", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Ajuste excluido", null));
    }
}
