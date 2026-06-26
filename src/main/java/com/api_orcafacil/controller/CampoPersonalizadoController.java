package com.api_orcafacil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api_orcafacil.common.TipoCampo;
import com.api_orcafacil.common.TipoCampoValor;
import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.precificacao.CampoPersonalizadoRequest;
import com.api_orcafacil.model.CampoPersonalizado;
import com.api_orcafacil.service.CampoPersonalizadoService;

@RestController
@RequestMapping("/campos-personalizados")
public class CampoPersonalizadoController {

    private final CampoPersonalizadoService service;

    public CampoPersonalizadoController(CampoPersonalizadoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CampoPersonalizado>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping({ "/tipo-valor", "/tipo-valor/" })
    public ResponseEntity<ApiResponseDTO<TipoCampoValor[]>> listarTiposValor() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", TipoCampoValor.values()));
    }

    @GetMapping({ "/tipo-campo", "/tipo-campo/" })
    public ResponseEntity<ApiResponseDTO<TipoCampo[]>> listarTiposCampo() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", TipoCampo.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CampoPersonalizado>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<CampoPersonalizado>> criar(@RequestBody CampoPersonalizadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Campo salvo", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CampoPersonalizado>> atualizar(@PathVariable Long id,
            @RequestBody CampoPersonalizadoRequest request) {
        request.setIdCampoPersonalizado(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Campo atualizado", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Campo excluido", null));
    }
}
