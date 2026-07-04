package com.api_orcafacil.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.organizacao.OrganizacaoEmpresaDTO;
import com.api_orcafacil.dto.organizacao.OrganizacaoEmpresaRequestDTO;
import com.api_orcafacil.dto.orcamento.ConfiguracaoOrcamentoRequest;
import com.api_orcafacil.model.ConfiguracaoOrcamento;
import com.api_orcafacil.service.ConfiguracaoOrcamentoService;
import com.api_orcafacil.service.OrganizacaoEmpresaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/configuracao-orcamento")
public class ConfiguracaoOrcamentoController {

    private final ConfiguracaoOrcamentoService service;
    private final OrganizacaoEmpresaService empresaService;

    public ConfiguracaoOrcamentoController(
            ConfiguracaoOrcamentoService service,
            OrganizacaoEmpresaService empresaService) {
        this.service = service;
        this.empresaService = empresaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<ConfiguracaoOrcamento>> obter() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.obter()));
    }

    @PutMapping
    public ResponseEntity<ApiResponseDTO<ConfiguracaoOrcamento>> salvar(
            @Valid @RequestBody ConfiguracaoOrcamentoRequest request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Configuracao salva", service.salvar(request)));
    }

    @GetMapping("/empresa")
    public ResponseEntity<ApiResponseDTO<OrganizacaoEmpresaDTO>> obterDadosEmpresa() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", empresaService.obterAtual()));
    }

    @PutMapping("/empresa")
    public ResponseEntity<ApiResponseDTO<OrganizacaoEmpresaDTO>> salvarDadosEmpresa(
            @Valid @RequestBody OrganizacaoEmpresaRequestDTO request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Dados da empresa atualizados", empresaService.salvar(request)));
    }
}
