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
import org.springframework.web.server.ResponseStatusException;

import com.api_orcafacil.domain.orcamento.dto.OrcamentoVisualizacaoDTO;
import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoRepository;
import com.api_orcafacil.domain.orcamento.service.CondicaoPagamentoService;
import com.api_orcafacil.domain.orcamento.service.OrcamentoService;
import com.api_orcafacil.domain.orcamento.service.VisualizacaoOrcamentoService;
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

    @Autowired
    private OrcamentoRepository repository;

    @Autowired
    private VisualizacaoOrcamentoService visualizacaoOrcamento;

    public OrcamentoController(BaseRepository<Orcamento, Long> repository) {
        super(repository);
    }

    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody Orcamento objeto) throws Exception {

        service.salvar(objeto);
        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);
    }

    @PostMapping(value = "/rascunho", produces = "application/json")
    public ResponseEntity<?> rascunho(@RequestBody Orcamento objeto) throws Exception {

        objeto.setTpStatus(StatusOrcamento.RASCUNHO);
        objeto = service.salvar(objeto);

        return new ResponseEntity<>(Map.of("message", "Registro Enviado com sucesso"), HttpStatus.CREATED);
    }

    @PostMapping(value = "/gerar", produces = "application/json")
    public ResponseEntity<?> gerar(@RequestBody Orcamento objeto) throws Exception {
        objeto.setTpStatus(StatusOrcamento.GERADO);
        objeto = service.salvar(objeto);

        return new ResponseEntity<>(
                Map.of("message", "Registro Gerado com sucesso", "cdPublico", objeto.getCdPublico()),
                HttpStatus.CREATED);
    }

    @PostMapping(value = "/enviar", produces = "application/json")
    public ResponseEntity<?> enviar(@RequestBody Orcamento objeto) throws Exception {

        service.alterarStatus(
                objeto.getIdOrcamento(),
                StatusOrcamento.ENVIADO);
        return new ResponseEntity<>(Map.of("message", "Registro Enviado com sucesso"), HttpStatus.CREATED);
    }

    @PostMapping(value = "/aprovar", produces = "application/json")
    public ResponseEntity<?> aprovar(@RequestBody Orcamento objeto) throws Exception {

        service.alterarStatus(
                objeto.getIdOrcamento(),
                StatusOrcamento.APROVADO);
        return new ResponseEntity<>(Map.of("message", "Registro aprovado com sucesso"), HttpStatus.CREATED);
    }

    @PostMapping(value = "/rejeitar", produces = "application/json")
    public ResponseEntity<?> rejeitar(@RequestBody Orcamento objeto) throws Exception {

        service.alterarStatus(
                objeto.getIdOrcamento(),
                StatusOrcamento.REJEITADO);
        return new ResponseEntity<>(Map.of("message", "Registro rejeitado com sucesso"), HttpStatus.CREATED);
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

    @GetMapping(value = "/visualizacao/{cdPublico}", produces = "application/json")
    public ResponseEntity<OrcamentoVisualizacaoDTO> visualizacao(
            @PathVariable("cdPublico") String cdPublico) {

        Orcamento orcamento = repository
                .findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Orçamento não encontrado"));

        if (orcamento.getTpStatus() == StatusOrcamento.RASCUNHO) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        OrcamentoVisualizacaoDTO dto = visualizacaoOrcamento.visualizarPublico(orcamento.getIdOrcamento(),
                orcamento.getIdTenant());

        return ResponseEntity.ok(dto);
    }
}
