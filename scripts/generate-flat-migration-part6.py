#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "src" / "main" / "java" / "com" / "api_orcafacil"
CONFIG = Path(__file__).resolve().parent.parent / "src" / "main" / "java" / "com" / "api_orcafacil" / "config" / "SwaggerConfig.java"
FILES = {}

def add(p, c):
    FILES[p] = c

CTRL = '''package com.api_orcafacil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api_orcafacil.dto.ApiResponseDTO;
'''

add("controller/ClienteController.java", CTRL + '''import com.api_orcafacil.dto.cliente.ClienteRequest;
import com.api_orcafacil.dto.cliente.ClienteResponse;
import com.api_orcafacil.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ClienteResponse>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ClienteResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ClienteResponse>> criar(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Cliente salvo", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ClienteResponse>> atualizar(@PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        request.setIdCliente(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Cliente atualizado", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Cliente excluido", null));
    }
}
''')

add("controller/CatalogoController.java", CTRL + '''import com.api_orcafacil.dto.catalogo.CatalogoRequest;
import com.api_orcafacil.dto.catalogo.CatalogoResponse;
import com.api_orcafacil.service.CatalogoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/catalogos")
public class CatalogoController {

    private final CatalogoService service;

    public CatalogoController(CatalogoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CatalogoResponse>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CatalogoResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @GetMapping("/sequencia")
    public ResponseEntity<ApiResponseDTO<String>> sequencia() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.sequencia()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<CatalogoResponse>> criar(@Valid @RequestBody CatalogoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Catalogo salvo", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CatalogoResponse>> atualizar(@PathVariable Long id,
            @Valid @RequestBody CatalogoRequest request) {
        request.setIdCatalogo(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Catalogo atualizado", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Catalogo excluido", null));
    }
}
''')

add("controller/ServicoController.java", CTRL + '''import com.api_orcafacil.dto.servico.ServicoRequest;
import com.api_orcafacil.dto.servico.ServicoResponse;
import com.api_orcafacil.service.ServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ServicoResponse>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ServicoResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @GetMapping("/sequencia")
    public ResponseEntity<ApiResponseDTO<String>> sequencia() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.sequencia()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ServicoResponse>> criar(@Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Servico salvo", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ServicoResponse>> atualizar(@PathVariable Long id,
            @Valid @RequestBody ServicoRequest request) {
        request.setIdServico(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Servico atualizado", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Servico excluido", null));
    }
}
''')

add("controller/CategoriaServicoController.java", CTRL + '''import com.api_orcafacil.model.CategoriaServico;
import com.api_orcafacil.service.CategoriaServicoService;

@RestController
@RequestMapping("/categorias-servico")
public class CategoriaServicoController {

    private final CategoriaServicoService service;

    public CategoriaServicoController(CategoriaServicoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CategoriaServico>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CategoriaServico>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @GetMapping("/sequencia")
    public ResponseEntity<ApiResponseDTO<String>> sequencia() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.sequencia()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<CategoriaServico>> criar(@RequestBody CategoriaServico request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Categoria salva", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CategoriaServico>> atualizar(@PathVariable Long id,
            @RequestBody CategoriaServico request) {
        request.setIdCategoriaServico(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Categoria atualizada", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Categoria excluida", null));
    }
}
''')

add("controller/OrcamentoController.java", '''package com.api_orcafacil.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.orcamento.OrcamentoRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoResponse;
import com.api_orcafacil.dto.orcamento.OrcamentoVisualizacaoDTO;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.relatorio.RelatorioOrcamentoService;
import com.api_orcafacil.repository.OrcamentoRepository;
import com.api_orcafacil.service.OrcamentoService;
import com.api_orcafacil.service.VisualizacaoOrcamentoService;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    private final OrcamentoService service;
    private final OrcamentoRepository repository;
    private final VisualizacaoOrcamentoService visualizacaoOrcamentoService;
    private final RelatorioOrcamentoService relatorioOrcamentoService;

    public OrcamentoController(OrcamentoService service, OrcamentoRepository repository,
            VisualizacaoOrcamentoService visualizacaoOrcamentoService,
            RelatorioOrcamentoService relatorioOrcamentoService) {
        this.service = service;
        this.repository = repository;
        this.visualizacaoOrcamentoService = visualizacaoOrcamentoService;
        this.relatorioOrcamentoService = relatorioOrcamentoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<OrcamentoResponse>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @GetMapping("/sequencia")
    public ResponseEntity<ApiResponseDTO<String>> sequencia() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.sequencia()));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponseDTO<StatusOrcamento[]>> status() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", StatusOrcamento.values()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> criar(@RequestBody OrcamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Orcamento salvo", service.salvar(request)));
    }

    @PostMapping("/rascunho")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> rascunho(@RequestBody OrcamentoRequest request) {
        request.setTpStatus(StatusOrcamento.RASCUNHO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Rascunho salvo", service.salvar(request)));
    }

    @PostMapping("/{id}/gerar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> gerar(@PathVariable Long id, @RequestBody OrcamentoRequest request) {
        request.setIdOrcamento(id);
        request.setTpStatus(StatusOrcamento.GERADO);
        OrcamentoResponse response = service.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Orcamento gerado", response));
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> enviar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento enviado", service.alterarStatus(id, StatusOrcamento.ENVIADO)));
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento aprovado", service.alterarStatus(id, StatusOrcamento.APROVADO)));
    }

    @PostMapping("/{id}/rejeitar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> rejeitar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento rejeitado", service.alterarStatus(id, StatusOrcamento.REJEITADO)));
    }

    @PostMapping("/preview-precificacao")
    public ResponseEntity<ApiResponseDTO<Map<String, BigDecimal>>> previewPrecificacao(@RequestBody OrcamentoRequest request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Preview calculado",
                Map.of("valorTotal", service.previewPrecificacao(request))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> atualizar(@PathVariable Long id, @RequestBody OrcamentoRequest request) {
        request.setIdOrcamento(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento atualizado", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento excluido", null));
    }

    @GetMapping("/visualizacao/{cdPublico}")
    public ResponseEntity<ApiResponseDTO<OrcamentoVisualizacaoDTO>> visualizacao(@PathVariable String cdPublico) {
        Orcamento orcamento = repository.findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orcamento nao encontrado"));
        if (orcamento.getTpStatus() == StatusOrcamento.RASCUNHO) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso",
                visualizacaoOrcamentoService.visualizarPorCdPublico(cdPublico)));
    }

    @GetMapping(value = "/relatorio/{cdPublico}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> relatorio(@PathVariable String cdPublico) throws Exception {
        byte[] pdf = relatorioOrcamentoService.gerarRelatorioOrcamento(cdPublico);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=orcamento.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
''')

add("controller/CondicaoPagamentoController.java", CTRL + '''import com.api_orcafacil.model.CondicaoPagamento;
import com.api_orcafacil.service.CondicaoPagamentoService;

@RestController
@RequestMapping("/condicoes-pagamento")
public class CondicaoPagamentoController {

    private final CondicaoPagamentoService service;

    public CondicaoPagamentoController(CondicaoPagamentoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CondicaoPagamento>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CondicaoPagamento>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @GetMapping("/sequencia")
    public ResponseEntity<ApiResponseDTO<String>> sequencia() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.sequencia()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<CondicaoPagamento>> criar(@RequestBody CondicaoPagamento request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Condicao salva", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CondicaoPagamento>> atualizar(@PathVariable Long id,
            @RequestBody CondicaoPagamento request) {
        request.setIdCondicaoPagamento(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Condicao atualizada", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Condicao excluida", null));
    }
}
''')

add("controller/ConfiguracaoOrcamentoController.java", CTRL + '''import com.api_orcafacil.model.ConfiguracaoOrcamento;
import com.api_orcafacil.service.ConfiguracaoOrcamentoService;

@RestController
@RequestMapping("/configuracao-orcamento")
public class ConfiguracaoOrcamentoController {

    private final ConfiguracaoOrcamentoService service;

    public ConfiguracaoOrcamentoController(ConfiguracaoOrcamentoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<ConfiguracaoOrcamento>> obter() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.obter()));
    }

    @PutMapping
    public ResponseEntity<ApiResponseDTO<ConfiguracaoOrcamento>> salvar(@RequestBody ConfiguracaoOrcamento request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Configuracao salva", service.salvar(request)));
    }
}
''')

add("controller/MetodoPrecificacaoController.java", CTRL + '''import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.dto.precificacao.MetodoPrecificacaoResponse;
import com.api_orcafacil.model.MetodoPrecificacao;
import com.api_orcafacil.service.MetodoPrecificacaoService;

@RestController
@RequestMapping("/metodos-precificacao")
public class MetodoPrecificacaoController {

    private final MetodoPrecificacaoService service;

    public MetodoPrecificacaoController(MetodoPrecificacaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<MetodoPrecificacaoResponse>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MetodoPrecificacaoResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @GetMapping("/tipos")
    public ResponseEntity<ApiResponseDTO<TipoPrecificacao[]>> tipos() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", TipoPrecificacao.values()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<MetodoPrecificacao>> criar(@RequestBody MetodoPrecificacao request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Metodo salvo", service.salvar(request)));
    }
}
''')

add("controller/CampoPersonalizadoController.java", CTRL + '''import com.api_orcafacil.dto.precificacao.CampoPersonalizadoRequest;
import com.api_orcafacil.model.CampoPersonalizado;
import com.api_orcafacil.service.CampoPersonalizadoService;

@RestController
@RequestMapping("/campos-personalizados")
public class CampoPersonalizadoController {

    private final CampoPersonalizadoService service;

    public CampoPersonalizadoController(CampoPersonalizadoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CampoPersonalizado>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CampoPersonalizado>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<CampoPersonalizado>> criar(@RequestBody CampoPersonalizadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Campo salvo", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CampoPersonalizado>> atualizar(@PathVariable Long id,
            @RequestBody CampoPersonalizadoRequest request) {
        request.setIdCampoPersonalizado(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Campo atualizado", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Campo excluido", null));
    }
}
''')

add("controller/MetodoAjusteController.java", CTRL + '''import com.api_orcafacil.dto.precificacao.MetodoAjusteRequest;
import com.api_orcafacil.model.MetodoAjuste;
import com.api_orcafacil.service.MetodoAjusteService;

@RestController
@RequestMapping("/metodos-ajuste")
public class MetodoAjusteController {

    private final MetodoAjusteService service;

    public MetodoAjusteController(MetodoAjusteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<MetodoAjuste>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MetodoAjuste>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<MetodoAjuste>> criar(@RequestBody MetodoAjusteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Ajuste salvo", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MetodoAjuste>> atualizar(@PathVariable Long id,
            @RequestBody MetodoAjusteRequest request) {
        request.setIdMetodoAjuste(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Ajuste atualizado", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Ajuste excluido", null));
    }
}
''')

add("controller/EmpresaMetodoPrecificacaoController.java", CTRL + '''import com.api_orcafacil.dto.precificacao.EmpresaMetodoPrecificacaoRequest;
import com.api_orcafacil.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.service.EmpresaMetodoPrecificacaoService;

@RestController
@RequestMapping("/empresa-metodos-precificacao")
public class EmpresaMetodoPrecificacaoController {

    private final EmpresaMetodoPrecificacaoService service;

    public EmpresaMetodoPrecificacaoController(EmpresaMetodoPrecificacaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<EmpresaMetodoPrecificacao>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmpresaMetodoPrecificacao>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<EmpresaMetodoPrecificacao>> criar(@RequestBody EmpresaMetodoPrecificacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Metodo salvo", service.salvar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmpresaMetodoPrecificacao>> atualizar(@PathVariable Long id,
            @RequestBody EmpresaMetodoPrecificacaoRequest request) {
        request.setIdEmpresaMetodoPrecificacao(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Metodo atualizado", service.salvar(request)));
    }
}
''')

add("controller/PerfilController.java", '''package com.api_orcafacil.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.perfil.PerfilRequest;
import com.api_orcafacil.dto.perfil.PerfilResponse;
import com.api_orcafacil.service.PerfilService;

@RestController
@RequestMapping("/perfil")
public class PerfilController {

    private final PerfilService service;

    public PerfilController(PerfilService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<PerfilResponse>> obter() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.obter()));
    }

    @PutMapping
    public ResponseEntity<ApiResponseDTO<Void>> atualizar(@RequestBody PerfilRequest request) {
        service.atualizar(request);
        return ResponseEntity.ok(new ApiResponseDTO<>("Perfil atualizado", null));
    }

    @PostMapping("/foto")
    public ResponseEntity<ApiResponseDTO<String>> uploadFoto(@RequestParam MultipartFile file) throws Exception {
        return ResponseEntity.ok(new ApiResponseDTO<>("Foto atualizada", service.uploadFoto(file)));
    }

    @DeleteMapping("/foto")
    public ResponseEntity<ApiResponseDTO<Void>> removerFoto() {
        service.removerFoto();
        return ResponseEntity.ok(new ApiResponseDTO<>("Foto removida", null));
    }
}
''')

add("controller/admin/PlanoAssinaturaAdminController.java", '''package com.api_orcafacil.controller.admin;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.precificacao.PlanoAssinaturaRequest;
import com.api_orcafacil.dto.precificacao.PlanoAssinaturaResponse;
import com.api_orcafacil.service.PlanoAssinaturaPlatformService;

@RestController
@RequestMapping("/admin/planos-assinatura")
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class PlanoAssinaturaAdminController {

    private final PlanoAssinaturaPlatformService service;

    public PlanoAssinaturaAdminController(PlanoAssinaturaPlatformService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PlanoAssinaturaResponse>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PlanoAssinaturaResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<PlanoAssinaturaResponse>> criar(@RequestBody PlanoAssinaturaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Plano criado", service.criar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PlanoAssinaturaResponse>> atualizar(@PathVariable Long id,
            @RequestBody PlanoAssinaturaRequest request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Plano atualizado", service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Plano excluido", null));
    }
}
''')

SWAGGER = '''package com.api_orcafacil.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration(proxyBeanMethods = false)
public class SwaggerConfig {

    @Bean
    OpenAPI customOpenAPI(@Value("${server.servlet.context-path:}") String contextPath) {
        final String securitySchemeName = "bearerAuth";
        String basePath = contextPath == null || contextPath.isBlank() ? "" : contextPath;

        return new OpenAPI()
                .info(new Info()
                        .title("API OrcaFacil")
                        .version("v1")
                        .description("API SaaS de orcamentos com autenticacao JWT"))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .servers(List.of(new Server().url("http://localhost:8080" + basePath)));
    }

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/**")
                .build();
    }
}
'''

if __name__ == "__main__":
    for rel, content in FILES.items():
        (ROOT / rel).parent.mkdir(parents=True, exist_ok=True)
        (ROOT / rel).write_text(content, encoding="utf-8")
        print(f"Wrote {rel}")
    CONFIG.write_text(SWAGGER, encoding="utf-8")
    print("Wrote config/SwaggerConfig.java")
    print(f"Total controllers: {len(FILES)}")
