package com.api_orcafacil.controller.empresa;



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

import com.api_orcafacil.controller.base.BaseController;
import com.api_orcafacil.controller.base.BaseControllerJpa;
import com.api_orcafacil.dto.usuario.UsuarioDTO;
import com.api_orcafacil.model.empresa.PlanoAssinatura;
import com.api_orcafacil.service.empresa.PlanoAssinaturaService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/planoassinatura", produces = "application/json")
@Tag(name = "Plano de assinatura")
public class PlanoAssinaturaController extends BaseControllerJpa<PlanoAssinatura, Long> {

    @Autowired
    private PlanoAssinaturaService service;

 
    public PlanoAssinaturaController(JpaRepository<PlanoAssinatura, Long> repository) {
        super(repository);
    }

  
    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody PlanoAssinatura objeto) throws Exception {

        PlanoAssinatura objetoSalvo =  service.salvar(objeto);

        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);
    }

 

}
