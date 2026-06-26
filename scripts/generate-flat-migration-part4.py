#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "src" / "main" / "java" / "com" / "api_orcafacil"
FILES = {}

def add(p, c):
    FILES[p] = c

add("service/ClienteService.java", '''package com.api_orcafacil.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.common.TipoCliente;
import com.api_orcafacil.dto.cliente.ClienteRequest;
import com.api_orcafacil.dto.cliente.ClienteResponse;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Cliente;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final TenantContextService tenantContextService;

    public ClienteService(ClienteRepository repository, TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public List<ClienteResponse> listar() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        return repository.findByIdOrganizacao(idOrganizacao).stream().map(ClienteResponse::from).toList();
    }

    public ClienteResponse buscar(Long id) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Cliente cliente = repository.findByIdClienteAndIdOrganizacao(id, idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"));
        return ClienteResponse.from(cliente);
    }

    @Transactional
    public ClienteResponse salvar(ClienteRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        validarDuplicidade(request.getNuCpfcnpj(), idOrganizacao, request.getIdCliente());

        Cliente cliente = request.getIdCliente() != null
                ? repository.findByIdClienteAndIdOrganizacao(request.getIdCliente(), idOrganizacao)
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"))
                : new Cliente();

        aplicar(cliente, request, idOrganizacao);
        return ClienteResponse.from(repository.save(cliente));
    }

    @Transactional
    public void excluir(Long id) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Cliente cliente = repository.findByIdClienteAndIdOrganizacao(id, idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"));
        repository.delete(cliente);
    }

    @Transactional
    public Cliente registrarClienteAPartirDoOrcamento(Orcamento orcamento) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Cliente entrada = orcamento.getCliente();
        if (entrada == null || entrada.getNuCpfcnpj() == null || entrada.getNuCpfcnpj().isBlank()) {
            throw new ConflictException("CPF/CNPJ do cliente e obrigatorio");
        }

        Cliente cliente = repository.findByNuCpfcnpjAndIdOrganizacao(entrada.getNuCpfcnpj(), idOrganizacao)
                .orElseGet(Cliente::new);

        cliente.setIdOrganizacao(idOrganizacao);
        cliente.setTpCliente(entrada.getNuCpfcnpj().length() > 11 ? TipoCliente.Juridico : TipoCliente.Fisico);
        cliente.setNuCpfcnpj(entrada.getNuCpfcnpj());
        cliente.setNmCliente(entrada.getNmCliente());
        cliente.setNuTelefone(entrada.getNuTelefone());
        cliente.setDsEmail(entrada.getDsEmail());
        cliente.setDsObservacoes(entrada.getDsObservacoes());

        cliente = repository.save(cliente);
        orcamento.setCliente(cliente);
        orcamento.setIdCliente(cliente.getIdCliente());
        return cliente;
    }

    private void aplicar(Cliente cliente, ClienteRequest request, Long idOrganizacao) {
        cliente.setIdOrganizacao(idOrganizacao);
        cliente.setTpCliente(request.getTpCliente());
        cliente.setNuCpfcnpj(request.getNuCpfcnpj());
        cliente.setNmCliente(request.getNmCliente());
        cliente.setDsEmail(request.getDsEmail());
        cliente.setNuTelefone(request.getNuTelefone());
        cliente.setNuCep(request.getNuCep());
        cliente.setDsLogradouro(request.getDsLogradouro());
        cliente.setDsComplemento(request.getDsComplemento());
        cliente.setDsBairro(request.getDsBairro());
        cliente.setDsCidade(request.getDsCidade());
        cliente.setDsEstado(request.getDsEstado());
        if (request.getFlAtivo() != null) {
            cliente.setFlAtivo(request.getFlAtivo());
        }
        cliente.setDsObservacoes(request.getDsObservacoes());
    }

    private void validarDuplicidade(String nuCpfcnpj, Long idOrganizacao, Long idAtual) {
        Optional<Cliente> existente = repository.findByNuCpfcnpjAndIdOrganizacao(nuCpfcnpj, idOrganizacao);
        if (existente.isPresent() && !existente.get().getIdCliente().equals(idAtual)) {
            throw new ConflictException("CPF/CNPJ ja cadastrado para outro cliente");
        }
    }
}
''')

add("service/CategoriaServicoService.java", '''package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.CategoriaServico;
import com.api_orcafacil.repository.CategoriaServicoRepository;

@Service
public class CategoriaServicoService {

    private final CategoriaServicoRepository repository;
    private final TenantContextService tenantContextService;

    public CategoriaServicoService(CategoriaServicoRepository repository, TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public List<CategoriaServico> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
    }

    public CategoriaServico buscar(Long id) {
        return repository.findByIdCategoriaServicoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada"));
    }

    @Transactional
    public CategoriaServico salvar(CategoriaServico objeto) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        objeto.setIdOrganizacao(idOrganizacao);
        validarCodigo(objeto);
        return repository.save(objeto);
    }

    @Transactional
    public void excluir(Long id) {
        CategoriaServico categoria = buscar(id);
        repository.delete(categoria);
    }

    public String sequencia() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        return SequenciaUtil.gerarSequencia(repository.obterSequencial(idOrganizacao));
    }

    private void validarCodigo(CategoriaServico objeto) {
        repository.findByCdCategoriaServicoAndIdOrganizacao(objeto.getCdCategoriaServico(), objeto.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdCategoriaServico().equals(objeto.getIdCategoriaServico())) {
                        throw new ConflictException("Codigo de categoria ja cadastrado");
                    }
                });
    }
}
''')

add("service/ServicoService.java", '''package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.dto.servico.ServicoRequest;
import com.api_orcafacil.dto.servico.ServicoResponse;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Servico;
import com.api_orcafacil.repository.ServicoRepository;

@Service
public class ServicoService {

    private final ServicoRepository repository;
    private final TenantContextService tenantContextService;

    public ServicoService(ServicoRepository repository, TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public List<ServicoResponse> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria())
                .stream().map(ServicoResponse::from).toList();
    }

    public ServicoResponse buscar(Long id) {
        Servico servico = repository.findByIdServicoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Servico nao encontrado"));
        return ServicoResponse.from(servico);
    }

    @Transactional
    public ServicoResponse salvar(ServicoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Servico servico = request.getIdServico() != null
                ? repository.findByIdServicoAndIdOrganizacao(request.getIdServico(), idOrganizacao)
                        .orElseThrow(() -> new ResourceNotFoundException("Servico nao encontrado"))
                : new Servico();
        servico.setIdOrganizacao(idOrganizacao);
        servico.setIdCategoriaServico(request.getIdCategoriaServico());
        servico.setCdServico(request.getCdServico());
        servico.setNmServico(request.getNmServico());
        servico.setDsServico(request.getDsServico());
        servico.setVlCusto(request.getVlCusto());
        servico.setVlPreco(request.getVlPreco());
        validarCodigo(servico);
        return ServicoResponse.from(repository.save(servico));
    }

    @Transactional
    public void excluir(Long id) {
        Servico servico = repository.findByIdServicoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Servico nao encontrado"));
        repository.delete(servico);
    }

    public String sequencia() {
        return SequenciaUtil.gerarSequencia(repository.obterSequencial(tenantContextService.idOrganizacaoObrigatoria()));
    }

    private void validarCodigo(Servico servico) {
        repository.findByCdServicoAndIdOrganizacao(servico.getCdServico(), servico.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdServico().equals(servico.getIdServico())) {
                        throw new ConflictException("Codigo de servico ja cadastrado");
                    }
                });
    }
}
''')

add("service/CatalogoService.java", '''package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.common.TipoItem;
import com.api_orcafacil.dto.catalogo.CatalogoRequest;
import com.api_orcafacil.dto.catalogo.CatalogoResponse;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Catalogo;
import com.api_orcafacil.model.CatalogoCampo;
import com.api_orcafacil.repository.CatalogoCampoRepository;
import com.api_orcafacil.repository.CatalogoRepository;
import com.api_orcafacil.util.MestreDetalheUtils;

@Service
public class CatalogoService {

    private final CatalogoRepository repository;
    private final CatalogoCampoRepository campoRepository;
    private final TenantContextService tenantContextService;

    public CatalogoService(CatalogoRepository repository, CatalogoCampoRepository campoRepository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.campoRepository = campoRepository;
        this.tenantContextService = tenantContextService;
    }

    public List<CatalogoResponse> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria())
                .stream().map(CatalogoResponse::from).toList();
    }

    public CatalogoResponse buscar(Long id) {
        Catalogo catalogo = repository.findByIdCatalogoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo nao encontrado"));
        return CatalogoResponse.from(catalogo);
    }

    @Transactional
    public CatalogoResponse salvar(CatalogoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Catalogo catalogo = request.getIdCatalogo() != null
                ? repository.findByIdCatalogoAndIdOrganizacao(request.getIdCatalogo(), idOrganizacao)
                        .orElseThrow(() -> new ResourceNotFoundException("Catalogo nao encontrado"))
                : new Catalogo();

        catalogo.setIdOrganizacao(idOrganizacao);
        catalogo.setTpItem(request.getTpItem() != null ? request.getTpItem() : TipoItem.Produto);
        catalogo.setCdCatalogo(request.getCdCatalogo());
        catalogo.setNmCatalogo(request.getNmCatalogo());
        catalogo.setDsCatalogo(request.getDsCatalogo());
        catalogo.setVlCustoBase(request.getVlCustoBase());
        catalogo.setVlPrecoBase(request.getVlPrecoBase());

        validarCodigo(catalogo);
        catalogo = repository.save(catalogo);
        salvarCampos(catalogo, request.getCampos());
        return CatalogoResponse.from(repository.findById(catalogo.getIdCatalogo()).orElseThrow());
    }

    @Transactional
    public void excluir(Long id) {
        Catalogo catalogo = repository.findByIdCatalogoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo nao encontrado"));
        campoRepository.deleteByIdCatalogo(catalogo.getIdCatalogo());
        repository.delete(catalogo);
    }

    public String sequencia() {
        return SequenciaUtil.gerarSequencia(repository.obterSequencial(tenantContextService.idOrganizacaoObrigatoria()));
    }

    private void salvarCampos(Catalogo catalogo, List<CatalogoCampo> campos) {
        MestreDetalheUtils.removerItensGenerico(
                catalogo.getIdCatalogo(),
                campos,
                campoRepository::findByCatalogo_IdCatalogo,
                campoRepository::deleteById,
                CatalogoCampo::getIdCatalogoCampo);

        if (campos == null) {
            return;
        }
        for (CatalogoCampo campo : campos) {
            campo.setCatalogo(catalogo);
            if (campo.getIdCatalogoCampo() != null && campo.getIdCatalogoCampo() == 0) {
                campo.setIdCatalogoCampo(null);
            }
            campoRepository.save(campo);
        }
        catalogo.setCampos(campos);
    }

    private void validarCodigo(Catalogo catalogo) {
        repository.findByCdCatalogoAndIdOrganizacao(catalogo.getCdCatalogo(), catalogo.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdCatalogo().equals(catalogo.getIdCatalogo())) {
                        throw new ConflictException("Codigo de catalogo ja cadastrado");
                    }
                });
    }
}
''')

add("service/MetodoPrecificacaoService.java", '''package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.dto.precificacao.CampoMetodoDTO;
import com.api_orcafacil.dto.precificacao.MetodoPrecificacaoResponse;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.MetodoPrecificacao;
import com.api_orcafacil.repository.MetodoPrecificacaoRepository;

@Service
public class MetodoPrecificacaoService {

    private final MetodoPrecificacaoRepository repository;

    public MetodoPrecificacaoService(MetodoPrecificacaoRepository repository) {
        this.repository = repository;
    }

    public List<MetodoPrecificacaoResponse> listar() {
        return repository.findAll().stream().map(this::montar).toList();
    }

    public MetodoPrecificacaoResponse buscar(Long id) {
        return montar(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Metodo nao encontrado")));
    }

    @Transactional
    public MetodoPrecificacao salvar(MetodoPrecificacao objeto) {
        return repository.save(objeto);
    }

    public MetodoPrecificacaoResponse montar(MetodoPrecificacao metodo) {
        return MetodoPrecificacaoResponse.from(metodo, obterCamposPorTipo(metodo.getCdMetodoPrecificacao()));
    }

    private List<CampoMetodoDTO> obterCamposPorTipo(TipoPrecificacao tipo) {
        return switch (tipo) {
            case MARKUP -> List.of(new CampoMetodoDTO("percentual", "Percentual de Markup", "NUMBER", true));
            case MARGEM -> List.of(new CampoMetodoDTO("percentual", "Percentual de Margem", "NUMBER", true));
            case FIXO -> List.of(new CampoMetodoDTO("valor", "Valor Fixo", "NUMBER", true));
            case SIMPLES -> List.of();
        };
    }
}
''')

add("service/CampoPersonalizadoService.java", '''package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.precificacao.CampoPersonalizadoRequest;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.CampoPersonalizado;
import com.api_orcafacil.repository.CampoPersonalizadoRepository;

@Service
public class CampoPersonalizadoService {

    private final CampoPersonalizadoRepository repository;
    private final TenantContextService tenantContextService;

    public CampoPersonalizadoService(CampoPersonalizadoRepository repository, TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public List<CampoPersonalizado> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
    }

    public CampoPersonalizado buscar(Long id) {
        return repository.findByIdCampoPersonalizadoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Campo nao encontrado"));
    }

    @Transactional
    public CampoPersonalizado salvar(CampoPersonalizadoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        CampoPersonalizado campo = request.getIdCampoPersonalizado() != null
                ? buscar(request.getIdCampoPersonalizado())
                : new CampoPersonalizado();
        campo.setIdOrganizacao(idOrganizacao);
        campo.setCdCampoPersonalizado(request.getCdCampoPersonalizado());
        campo.setNmCampoPersonalizado(request.getNmCampoPersonalizado());
        campo.setDsCampoPersonalizado(request.getDsCampoPersonalizado());
        campo.setTpCampoPersonalizado(request.getTpCampoPersonalizado());
        if (request.getTpCampoValor() != null) {
            campo.setTpCampoValor(request.getTpCampoValor());
        }
        validarCodigo(campo);
        return repository.save(campo);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    private void validarCodigo(CampoPersonalizado campo) {
        repository.findByCdCampoPersonalizadoAndIdOrganizacao(campo.getCdCampoPersonalizado(), campo.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdCampoPersonalizado().equals(campo.getIdCampoPersonalizado())) {
                        throw new ConflictException("Codigo de campo ja cadastrado");
                    }
                });
    }
}
''')

add("service/EmpresaMetodoPrecificacaoService.java", '''package com.api_orcafacil.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.dto.precificacao.EmpresaMetodoPrecificacaoRequest;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.model.MetodoPrecificacao;
import com.api_orcafacil.repository.EmpresaMetodoPrecificacaoRepository;
import com.api_orcafacil.repository.MetodoPrecificacaoRepository;

@Service
public class EmpresaMetodoPrecificacaoService {

    private final EmpresaMetodoPrecificacaoRepository repository;
    private final MetodoPrecificacaoRepository metodoPrecificacaoRepository;
    private final TenantContextService tenantContextService;

    public EmpresaMetodoPrecificacaoService(EmpresaMetodoPrecificacaoRepository repository,
            MetodoPrecificacaoRepository metodoPrecificacaoRepository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.metodoPrecificacaoRepository = metodoPrecificacaoRepository;
        this.tenantContextService = tenantContextService;
    }

    public List<EmpresaMetodoPrecificacao> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
    }

    public EmpresaMetodoPrecificacao buscarPorId(Long id) {
        return repository.findByIdEmpresaMetodoPrecificacaoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa metodo nao encontrado"));
    }

    @Transactional
    public EmpresaMetodoPrecificacao salvar(EmpresaMetodoPrecificacaoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        EmpresaMetodoPrecificacao objeto = request.getIdEmpresaMetodoPrecificacao() != null
                ? buscarPorId(request.getIdEmpresaMetodoPrecificacao())
                : new EmpresaMetodoPrecificacao();
        objeto.setIdOrganizacao(idOrganizacao);
        objeto.setIdMetodoPrecificacao(request.getIdMetodoPrecificacao());
        objeto.setConfiguracao(request.getConfiguracao());
        validarDuplicidade(objeto);
        return repository.save(objeto);
    }

    public EmpresaMetodoPrecificacao obterEmpresaMetodoPrecificacaoSimples() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        MetodoPrecificacao metodoSimples = metodoPrecificacaoRepository.findByCdMetodoPrecificacao(TipoPrecificacao.SIMPLES)
                .orElseThrow(() -> new ResourceNotFoundException("Metodo SIMPLES nao cadastrado"));
        return repository.findByIdOrganizacaoAndIdMetodoPrecificacao(idOrganizacao, metodoSimples.getIdMetodoPrecificacao())
                .orElseGet(() -> repository.save(criarPadrao(idOrganizacao, metodoSimples.getIdMetodoPrecificacao())));
    }

    private EmpresaMetodoPrecificacao criarPadrao(Long idOrganizacao, Long idMetodo) {
        EmpresaMetodoPrecificacao cfg = new EmpresaMetodoPrecificacao();
        cfg.setIdOrganizacao(idOrganizacao);
        cfg.setIdMetodoPrecificacao(idMetodo);
        cfg.setConfiguracao(Map.of());
        return cfg;
    }

    private void validarDuplicidade(EmpresaMetodoPrecificacao objeto) {
        repository.findByMetodoAndOrganizacao(objeto.getIdMetodoPrecificacao(), objeto.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdEmpresaMetodoPrecificacao().equals(objeto.getIdEmpresaMetodoPrecificacao())) {
                        throw new ConflictException("Metodo de precificacao ja cadastrado para esta organizacao");
                    }
                });
    }
}
''')

add("service/MetodoAjusteService.java", '''package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.precificacao.MetodoAjusteRequest;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.MetodoAjuste;
import com.api_orcafacil.repository.MetodoAjusteRepository;

@Service
public class MetodoAjusteService {

    private final MetodoAjusteRepository repository;
    private final EmpresaMetodoPrecificacaoService empresaMetodoPrecificacaoService;
    private final TenantContextService tenantContextService;

    public MetodoAjusteService(MetodoAjusteRepository repository,
            EmpresaMetodoPrecificacaoService empresaMetodoPrecificacaoService,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.empresaMetodoPrecificacaoService = empresaMetodoPrecificacaoService;
        this.tenantContextService = tenantContextService;
    }

    public List<MetodoAjuste> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
    }

    public MetodoAjuste buscar(Long id) {
        return repository.findByIdMetodoAjusteAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Ajuste nao encontrado"));
    }

    @Transactional
    public MetodoAjuste salvar(MetodoAjusteRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        MetodoAjuste objeto = request.getIdMetodoAjuste() != null ? buscar(request.getIdMetodoAjuste()) : new MetodoAjuste();
        objeto.setIdOrganizacao(idOrganizacao);
        objeto.setIdEmpresaMetodoPrecificacao(request.getIdEmpresaMetodoPrecificacao() != null
                ? request.getIdEmpresaMetodoPrecificacao()
                : empresaMetodoPrecificacaoService.obterEmpresaMetodoPrecificacaoSimples().getIdEmpresaMetodoPrecificacao());
        objeto.setIdCampoPersonalizado(request.getIdCampoPersonalizado());
        objeto.setTpAjuste(request.getTpAjuste());
        objeto.setTpOperacao(request.getTpOperacao());
        objeto.setVlCondicao(request.getVlCondicao());
        objeto.setVlIncremento(request.getVlIncremento());
        validarCampo(objeto);
        return repository.save(objeto);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    private void validarCampo(MetodoAjuste objeto) {
        repository.findByIdCampoPersonalizado(objeto.getIdCampoPersonalizado())
                .ifPresent(existente -> {
                    if (!existente.getIdMetodoAjuste().equals(objeto.getIdMetodoAjuste())) {
                        throw new ConflictException("Ja existe ajuste para este campo");
                    }
                });
    }
}
''')

add("service/CondicaoPagamentoService.java", '''package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.CondicaoPagamento;
import com.api_orcafacil.repository.CondicaoPagamentoRepository;

@Service
public class CondicaoPagamentoService {

    private final CondicaoPagamentoRepository repository;
    private final TenantContextService tenantContextService;

    public CondicaoPagamentoService(CondicaoPagamentoRepository repository, TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public List<CondicaoPagamento> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
    }

    public CondicaoPagamento buscar(Long id) {
        return repository.findByIdCondicaoPagamentoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Condicao nao encontrada"));
    }

    @Transactional
    public CondicaoPagamento salvar(CondicaoPagamento objeto) {
        objeto.setIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
        validarCodigo(objeto);
        return repository.save(objeto);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    public String sequencia() {
        return SequenciaUtil.gerarSequencia(repository.obterSequencial(tenantContextService.idOrganizacaoObrigatoria()));
    }

    private void validarCodigo(CondicaoPagamento objeto) {
        repository.findByCdCondicaoPagamentoAndIdOrganizacao(objeto.getCdCondicaoPagamento(), objeto.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdCondicaoPagamento().equals(objeto.getIdCondicaoPagamento())) {
                        throw new ConflictException("Codigo de condicao ja cadastrado");
                    }
                });
    }
}
''')

add("service/ConfiguracaoOrcamentoService.java", '''package com.api_orcafacil.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.model.ConfiguracaoOrcamento;
import com.api_orcafacil.repository.ConfiguracaoOrcamentoRepository;

@Service
public class ConfiguracaoOrcamentoService {

    private final ConfiguracaoOrcamentoRepository repository;
    private final TenantContextService tenantContextService;

    public ConfiguracaoOrcamentoService(ConfiguracaoOrcamentoRepository repository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public ConfiguracaoOrcamento obter() {
        return obterOuCriarPadrao();
    }

    @Transactional
    public ConfiguracaoOrcamento salvar(ConfiguracaoOrcamento dados) {
        ConfiguracaoOrcamento atual = obterOuCriarPadrao();
        atual.setPrefixoNumero(dados.getPrefixoNumero());
        atual.setValidadeDias(dados.getValidadeDias());
        atual.setTermosPadrao(dados.getTermosPadrao());
        return repository.save(atual);
    }

    public ConfiguracaoOrcamento obterPrimeiroObjeto() {
        return obterOuCriarPadrao();
    }

    private ConfiguracaoOrcamento obterOuCriarPadrao() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        return repository.findByIdOrganizacao(idOrganizacao).orElseGet(() -> {
            ConfiguracaoOrcamento cfg = new ConfiguracaoOrcamento();
            cfg.setIdOrganizacao(idOrganizacao);
            cfg.setPrefixoNumero("ORC");
            cfg.setValidadeDias(30);
            return repository.save(cfg);
        });
    }
}
''')

if __name__ == "__main__":
    for rel, content in FILES.items():
        (ROOT / rel).parent.mkdir(parents=True, exist_ok=True)
        (ROOT / rel).write_text(content, encoding="utf-8")
        print(f"Wrote {rel}")
    print(f"Total: {len(FILES)}")
