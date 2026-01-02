package com.api_orcafacil.domain.cliente.controller;

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

import com.api_orcafacil.domain.cliente.model.Cliente;
import com.api_orcafacil.domain.cliente.service.ClienteService;
import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.service.EmpresaService;
import com.api_orcafacil.domain.sistema.controller.BaseController;
import com.api_orcafacil.domain.sistema.controller.BaseControllerJpa;
import com.api_orcafacil.domain.sistema.controller.BaseControllerJpaTenant;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;
import com.api_orcafacil.domain.usuario.dto.UsuarioDTO;
import com.api_orcafacil.enums.TipoCliente;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/cliente")
@Tag(name = "Clientes")
public class ClienteController extends BaseControllerJpaTenant<Cliente, Long> {

    @Autowired
    private ClienteService service;

    public ClienteController(BaseRepository<Cliente, Long> repository) {
        super(repository);
    }

    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody Cliente objeto) throws Exception {

        service.salvar(objeto);
        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);
    }

    @GetMapping("/tipo-cliente/")
    public ResponseEntity<TipoCliente[]> obterCliente() {
        return ResponseEntity.ok(TipoCliente.values());
    }

}
