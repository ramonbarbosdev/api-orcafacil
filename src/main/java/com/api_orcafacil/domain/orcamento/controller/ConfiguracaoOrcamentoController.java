package com.api_orcafacil.domain.orcamento.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.orcamento.service.ConfiguracaoOrcamentoService;

@RestController
@RequestMapping("/configuracaoorcamento")
public class ConfiguracaoOrcamentoController {

    @Autowired
    private ConfiguracaoOrcamentoService service;

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<ConfiguracaoOrcamento> obter(@RequestHeader("X-Tenant-ID") String tenantId) {

        return ResponseEntity.ok(
                service.obterOuCriarPadrao(tenantId));
    }

    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<?> salvar(
            @RequestBody ConfiguracaoOrcamento dto, @RequestHeader("X-Tenant-ID") String tenantId) {

        service.salvar(tenantId, dto);

        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);

    }


}
