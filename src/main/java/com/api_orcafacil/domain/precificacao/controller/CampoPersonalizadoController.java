package com.api_orcafacil.domain.precificacao.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;
import com.api_orcafacil.domain.empresa.service.PlanoAssinaturaService;
import com.api_orcafacil.domain.precificacao.model.CampoPersonalizado;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.repository.CampoPersonalizadoRepository;
import com.api_orcafacil.domain.precificacao.repository.EmpresaMetodoPrecificacaoRepository;
import com.api_orcafacil.domain.precificacao.service.CampoPersonalizadoService;
import com.api_orcafacil.domain.precificacao.service.EmpresaMetodoPrecificacaoService;
import com.api_orcafacil.domain.precificacao.service.MetodoPrecificacaoService;
import com.api_orcafacil.domain.sistema.controller.BaseController;
import com.api_orcafacil.domain.sistema.controller.BaseControllerJpa;
import com.api_orcafacil.domain.usuario.dto.UsuarioDTO;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/campopersonalizado", produces = "application/json")
@Tag(name = "Campos Personalizado")
public class CampoPersonalizadoController extends BaseControllerJpa<CampoPersonalizado, Long> {

    @Autowired
    private CampoPersonalizadoService service;

    @Autowired
    private CampoPersonalizadoRepository repository;

    public CampoPersonalizadoController(JpaRepository<CampoPersonalizado, Long> repository) {
        super(repository);
    }

    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody CampoPersonalizado objeto,
            @RequestHeader("X-Tenant-ID") String tenantId) throws Exception {

        objeto.setIdTenant(tenantId);
        CampoPersonalizado objetoSalvo = service.salvar(objeto);

        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);
    }

    @GetMapping(value = "/obter-por-tenant", produces = "application/json")
    public ResponseEntity<?> obterPorTenant(@RequestHeader("X-Tenant-ID") String tenantId) throws Exception {

        List<CampoPersonalizado> objeto = repository.findAllByIdTenant(tenantId);

        return ResponseEntity.ok(objeto);
    }

}
