package com.api_orcafacil.domain.precificacao.controller;

import java.util.List;
import java.util.Map;

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

import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;
import com.api_orcafacil.domain.empresa.service.PlanoAssinaturaService;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacaoMetaDTO;
import com.api_orcafacil.domain.precificacao.repository.MetodoPrecificacaoRepository;
import com.api_orcafacil.domain.precificacao.service.MetodoPrecificacaoService;
import com.api_orcafacil.domain.sistema.controller.BaseController;
import com.api_orcafacil.domain.sistema.controller.BaseControllerJpa;
import com.api_orcafacil.domain.usuario.dto.UsuarioDTO;
import com.api_orcafacil.enums.TipoCliente;
import com.api_orcafacil.enums.TipoPrecificacao;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "metodoprecificacao", produces = "application/json")
@Tag(name = "Metodo Precificacao")
public class MetodoPrecificacaoController extends BaseControllerJpa<MetodoPrecificacao, Long> {

    @Autowired
    private MetodoPrecificacaoService service;

    @Autowired
    private MetodoPrecificacaoRepository repository;

    public MetodoPrecificacaoController(JpaRepository<MetodoPrecificacao, Long> repository) {
        super(repository);
    }

    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody MetodoPrecificacao objeto) throws Exception {

        MetodoPrecificacao objetoSalvo = service.salvar(objeto);

        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);
    }

    @GetMapping(value = "/buscar", produces = "application/json")
    public List<MetodoPrecificacaoMetaDTO> listar() {
        return repository.findAll()
                .stream()
                .map(service::montar)
                .toList();
    }

    @GetMapping("/tipo-precificacao")
    public ResponseEntity<TipoPrecificacao[]> obterTipoPrecificacao() {
        return ResponseEntity.ok(TipoPrecificacao.values());
    }

}
