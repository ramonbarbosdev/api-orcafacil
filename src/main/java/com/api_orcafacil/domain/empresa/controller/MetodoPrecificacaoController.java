package com.api_orcafacil.domain.empresa.controller;



import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.domain.empresa.model.MetodoPrecificacao;
import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;
import com.api_orcafacil.domain.empresa.service.MetodoPrecificacaoService;
import com.api_orcafacil.domain.empresa.service.PlanoAssinaturaService;
import com.api_orcafacil.domain.sistema.controller.BaseController;
import com.api_orcafacil.domain.sistema.controller.BaseControllerJpa;
import com.api_orcafacil.domain.usuario.dto.UsuarioDTO;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "metodoprecificacao", produces = "application/json")
@Tag(name = "Metodo Precificacao")
public class MetodoPrecificacaoController extends BaseControllerJpa<MetodoPrecificacao, Long> {

    @Autowired
    private MetodoPrecificacaoService service;

    public MetodoPrecificacaoController(JpaRepository<MetodoPrecificacao, Long> repository) {
        super(repository);
    }
  
    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody MetodoPrecificacao objeto) throws Exception {

        MetodoPrecificacao objetoSalvo =  service.salvar(objeto);

        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);
    }


    

 

}
