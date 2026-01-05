package com.api_orcafacil.domain.precificacao.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;
import com.api_orcafacil.domain.empresa.service.PlanoAssinaturaService;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.repository.EmpresaMetodoPrecificacaoRepository;
import com.api_orcafacil.domain.precificacao.service.EmpresaMetodoPrecificacaoService;
import com.api_orcafacil.domain.precificacao.service.MetodoPrecificacaoService;
import com.api_orcafacil.domain.sistema.controller.BaseController;
import com.api_orcafacil.domain.sistema.controller.BaseControllerJpa;
import com.api_orcafacil.domain.usuario.dto.UsuarioDTO;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "empresametodoprecificacao", produces = "application/json")
@Tag(name = "Empresa Metodo Precificacao")
public class EmpresaMetodoPrecificacaoController extends BaseControllerJpa<EmpresaMetodoPrecificacao, Long> {

    @Autowired
    private EmpresaMetodoPrecificacaoService service;

     @Autowired
    private EmpresaMetodoPrecificacaoRepository repository;

    public EmpresaMetodoPrecificacaoController(JpaRepository<EmpresaMetodoPrecificacao, Long> repository) {
        super(repository);
    }

    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody EmpresaMetodoPrecificacao objeto, @RequestHeader("X-Tenant-ID") String tenantId) throws Exception {

        EmpresaMetodoPrecificacao objetoSalvo = service.salvar(tenantId, objeto);

        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);
    }

    @GetMapping(value = "/obter-por-tenant", produces = "application/json")
    public ResponseEntity<?> obterPorTenant(@RequestHeader("X-Tenant-ID") String tenantId) throws Exception {

        EmpresaMetodoPrecificacao objeto = service.obterOuCriarPadrao(tenantId);

        return ResponseEntity.ok(objeto);
    }

}
