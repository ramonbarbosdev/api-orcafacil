package com.api_orcafacil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.model.ConfiguracaoOrcamento;
import com.api_orcafacil.service.ConfiguracaoOrcamentoService;

@RestController
@RequestMapping("/configuracao-orcamento")
public class ConfiguracaoOrcamentoController {

    private final ConfiguracaoOrcamentoService service;

    public ConfiguracaoOrcamentoController(ConfiguracaoOrcamentoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<ConfiguracaoOrcamento>> obter() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.obter()));
    }

    @PutMapping
    public ResponseEntity<ApiResponseDTO<ConfiguracaoOrcamento>> salvar(@RequestBody ConfiguracaoOrcamento request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Configuracao salva", service.salvar(request)));
    }
}
