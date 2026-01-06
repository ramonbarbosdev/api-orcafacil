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
import com.api_orcafacil.domain.precificacao.model.MetodoAjuste;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.repository.CampoPersonalizadoRepository;
import com.api_orcafacil.domain.precificacao.repository.EmpresaMetodoPrecificacaoRepository;
import com.api_orcafacil.domain.precificacao.repository.MetodoAjusteRepository;
import com.api_orcafacil.domain.precificacao.service.CampoPersonalizadoService;
import com.api_orcafacil.domain.precificacao.service.EmpresaMetodoPrecificacaoService;
import com.api_orcafacil.domain.precificacao.service.MetodoAjusteService;
import com.api_orcafacil.domain.precificacao.service.MetodoPrecificacaoService;
import com.api_orcafacil.domain.sistema.controller.BaseController;
import com.api_orcafacil.domain.sistema.controller.BaseControllerJpa;
import com.api_orcafacil.domain.usuario.dto.UsuarioDTO;
import com.api_orcafacil.enums.TipoAjuste;
import com.api_orcafacil.enums.TipoOperacaoAjuste;
import com.api_orcafacil.enums.TipoPrecificacao;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "metodoajuste", produces = "application/json")
@Tag(name = "Metodo Ajuste")
public class MetodoAjusteController extends BaseControllerJpa<MetodoAjuste, Long> {

    @Autowired
    private MetodoAjusteService service;

    @Autowired
    private MetodoAjusteRepository repository;

    public MetodoAjusteController(JpaRepository<MetodoAjuste, Long> repository) {
        super(repository);
    }

    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> cadastrar(@RequestBody MetodoAjuste objeto,
            @RequestHeader("X-Tenant-ID") String tenantId) throws Exception {

        objeto.setIdTenant(tenantId);
        MetodoAjuste objetoSalvo = service.salvar(objeto);

        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);
    }

    @GetMapping(value = "/obter-por-tenant", produces = "application/json")
    public ResponseEntity<?> obterPorTenant(@RequestHeader("X-Tenant-ID") String tenantId) throws Exception {

        List<MetodoAjuste> objeto = repository.findAllByIdTenant(tenantId);

        return ResponseEntity.ok(objeto);
    }

     @GetMapping("/tipo-ajuste")
    public ResponseEntity<TipoAjuste[]> obterTipoAjuste() {
        return ResponseEntity.ok(TipoAjuste.values());
    }
     @GetMapping("/tipo-operacao")
    public ResponseEntity<TipoOperacaoAjuste[]> obterTiposOperacao() {
        return ResponseEntity.ok(TipoOperacaoAjuste.values());
    }

}
