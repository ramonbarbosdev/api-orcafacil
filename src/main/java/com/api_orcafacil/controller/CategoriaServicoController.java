package com.api_orcafacil.controller;

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

import com.api_orcafacil.controller.base.BaseController;
import com.api_orcafacil.controller.base.BaseControllerJpa;
import com.api_orcafacil.controller.base.BaseControllerJpaTenant;
import com.api_orcafacil.dto.usuario.UsuarioDTO;
import com.api_orcafacil.model.CategoriaServico;
import com.api_orcafacil.model.empresa.Empresa;
import com.api_orcafacil.repository.base.BaseRepository;
import com.api_orcafacil.service.CategoriaServicoService;
import com.api_orcafacil.service.empresa.EmpresaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/categoriaservico")
@Tag(name = "Categoria de Servicos")
public class CategoriaServicoController extends BaseControllerJpaTenant<CategoriaServico, Long> {

    @Autowired
    private CategoriaServicoService service;

    public CategoriaServicoController(BaseRepository<CategoriaServico, Long> repository) {
        super(repository);
    }

    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody CategoriaServico objeto) throws Exception {

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
