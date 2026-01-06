package com.api_orcafacil.domain.orcamento.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.service.CondicaoPagamentoService;

import com.api_orcafacil.domain.sistema.controller.BaseControllerJpaTenant;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/condicaopagamento")
@Tag(name = "Condicao do Pagamento")
public class CondicaoPagamentoController extends BaseControllerJpaTenant<CodicaoPagamento, Long> {

    @Autowired
    private CondicaoPagamentoService service;

    public CondicaoPagamentoController(BaseRepository<CodicaoPagamento, Long> repository) {
        super(repository);
    }

    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody CodicaoPagamento objeto) throws Exception {

        service.salvar(objeto);
        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);
    }

    @GetMapping(value = "/sequencia", produces = "application/json")
    @Operation(summary = "Gerar sequencia")
    public ResponseEntity<?> obterSequencia() throws Exception {

        String resposta = service.sequencia();

        return new ResponseEntity<>(Map.of("sequencia", resposta), HttpStatus.OK);
    }

}
