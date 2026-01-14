package com.api_orcafacil.domain.orcamento.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.service.CondicaoPagamentoService;
import com.api_orcafacil.domain.orcamento.service.OrcamentoService;
import com.api_orcafacil.domain.sistema.controller.BaseControllerJpaTenant;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;
import com.api_orcafacil.enums.StatusOrcamento;
import com.api_orcafacil.enums.TipoCliente;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/orcamento")
@Tag(name = "Orçamento")
public class OrcamentoController extends BaseControllerJpaTenant<Orcamento, Long> {

    @Autowired
    private OrcamentoService service;

    public OrcamentoController(BaseRepository<Orcamento, Long> repository) {
        super(repository);
    }

    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody Orcamento objeto) throws Exception {

        service.salvar(objeto);
        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);
    }

    @PostMapping(value = "/preview-precificacao", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> previewPrecificacao(
            @RequestBody Orcamento request) {

        BigDecimal resposta = service.previewPrecificacao(request);

        return ResponseEntity.ok(Map.of("valorTotal", resposta));
    }

    @GetMapping(value = "/sequencia", produces = "application/json")
    @Operation(summary = "Gerar sequencia")
    public ResponseEntity<?> obterSequencia(@RequestHeader("X-Tenant-ID") String tenantId) throws Exception {

        String resposta = service.sequencia(tenantId);

        return new ResponseEntity<>(Map.of("sequencia", resposta), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {
        service.excluir(id);
        return new ResponseEntity<>(Map.of("message", "Registro deletado com sucesso"), HttpStatus.OK);
    }

    @GetMapping("/status-orcamento")
    public ResponseEntity<StatusOrcamento[]> obterCliente() {
        return ResponseEntity.ok(StatusOrcamento.values());
    }
}
