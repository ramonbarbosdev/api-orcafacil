package com.api_orcafacil.domain.servico.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.service.EmpresaService;
import com.api_orcafacil.domain.servico.model.CategoriaServico;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.servico.service.CategoriaServicoService;
import com.api_orcafacil.domain.servico.service.ServicoService;
import com.api_orcafacil.domain.sistema.controller.BaseController;
import com.api_orcafacil.domain.sistema.controller.BaseControllerJpa;
import com.api_orcafacil.domain.sistema.controller.BaseControllerJpaTenant;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;
import com.api_orcafacil.domain.usuario.dto.UsuarioDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/servico")
@Tag(name = "Servicos")
public class ServicoController extends BaseControllerJpaTenant<Servico, Long> {

    @Autowired
    private ServicoService service;

    public ServicoController(BaseRepository<Servico, Long> repository) {
        super(repository);
    }

    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody Servico objeto) throws Exception {

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
