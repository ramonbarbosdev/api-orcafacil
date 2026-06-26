#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "src" / "main" / "java" / "com" / "api_orcafacil"
FILES = {}

def add(p, c):
    FILES[p] = c

add("service/PrecificacaoService.java", '''package com.api_orcafacil.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.api_orcafacil.common.TipoCampoValor;
import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.model.MetodoPrecificacao;
import com.api_orcafacil.model.OrcamentoItem;
import com.api_orcafacil.model.OrcamentoItemCampoValor;

@Service
public class PrecificacaoService {

    public BigDecimal precificarItem(OrcamentoItem item, EmpresaMetodoPrecificacao empresaMetodo) {
        MetodoPrecificacao metodo = empresaMetodo.getMetodoPrecificacao();
        if (metodo == null || metodo.getCdMetodoPrecificacao() == null) {
            throw new IllegalArgumentException("Metodo de precificacao nao definido");
        }
        if (item.getQtItem() == null) {
            throw new IllegalArgumentException("Quantidade do item nao informada");
        }
        if (item.getVlCustoUnitario() == null) {
            throw new IllegalArgumentException("Custo unitario do item nao informado");
        }

        BigDecimal quantidade = item.getQtItem();
        BigDecimal baseCalculo = item.getVlCustoUnitario().multiply(quantidade);
        baseCalculo = regraTipoCalculo(item, baseCalculo, quantidade);

        BigDecimal precoFinal = aplicarMetodo(baseCalculo, metodo.getCdMetodoPrecificacao(), empresaMetodo.getConfiguracao());
        return precoFinal.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal regraTipoCalculo(OrcamentoItem item, BigDecimal baseCalculo, BigDecimal quantidade) {
        if (item.getCamposValor() == null) {
            return baseCalculo;
        }
        for (OrcamentoItemCampoValor campo : item.getCamposValor()) {
            if (campo.getVlInformado() == null || campo.getTpValor() == null) {
                continue;
            }
            BigDecimal valor = campo.getVlInformado();
            switch (campo.getTpValor()) {
                case PRECO_FIXO -> baseCalculo = baseCalculo.add(valor);
                case CUSTO_UNITARIO -> baseCalculo = baseCalculo.add(valor.multiply(quantidade));
                case AJUSTE_METODO -> { }
            }
        }
        return baseCalculo;
    }

    private BigDecimal aplicarMetodo(BigDecimal base, TipoPrecificacao tipo, Map<String, Object> config) {
        return switch (tipo) {
            case MARKUP -> base.multiply(BigDecimal.ONE.add(obterDecimal(config, "percentual")));
            case MARGEM -> base.divide(BigDecimal.ONE.subtract(obterDecimal(config, "percentual")), 4, RoundingMode.HALF_UP);
            case FIXO -> base.add(obterDecimal(config, "valor"));
            case SIMPLES -> base;
        };
    }

    private BigDecimal obterDecimal(Map<String, Object> config, String chave) {
        if (config == null || !config.containsKey(chave)) {
            throw new IllegalArgumentException("Configuracao obrigatoria nao encontrada: " + chave);
        }
        try {
            return new BigDecimal(config.get(chave).toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor invalido para configuracao: " + chave);
        }
    }
}
''')

add("service/OrcamentoStatusHistoricoService.java", '''package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.model.OrcamentoStatusHistorico;
import com.api_orcafacil.repository.OrcamentoStatusHistoricoRepository;

@Service
public class OrcamentoStatusHistoricoService {

    private final OrcamentoStatusHistoricoRepository repository;
    private final TenantContextService tenantContextService;

    public OrcamentoStatusHistoricoService(OrcamentoStatusHistoricoRepository repository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    @Transactional
    public void registrar(Orcamento orcamento, StatusOrcamento statusAnterior, StatusOrcamento statusNovo) {
        OrcamentoStatusHistorico historico = new OrcamentoStatusHistorico();
        historico.setIdOrganizacao(orcamento.getIdOrganizacao());
        historico.setIdOrcamento(orcamento.getIdOrcamento());
        historico.setOrcamento(orcamento);
        historico.setTpStatusAnterior(statusAnterior);
        historico.setTpStatusNovo(statusNovo);
        repository.save(historico);
    }

    public List<OrcamentoStatusHistorico> listarPorOrcamento(Long idOrcamento) {
        return repository.findByIdOrcamentoOrderByDtCriacaoAsc(idOrcamento);
    }

    @Transactional
    public void excluirPorIdOrcamento(Long idOrcamento) {
        repository.deleteByIdOrcamento(idOrcamento);
    }
}
''')

add("service/OrcamentoService.java", '''package com.api_orcafacil.service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.dto.orcamento.OrcamentoRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoResponse;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.model.OrcamentoItem;
import com.api_orcafacil.model.OrcamentoItemCampoValor;
import com.api_orcafacil.repository.OrcamentoRepository;

@Service
public class OrcamentoService {

    private final OrcamentoRepository repository;
    private final TenantContextService tenantContextService;
    private final ClienteService clienteService;
    private final ConfiguracaoOrcamentoService configuracaoOrcamentoService;
    private final PrecificacaoService precificacaoService;
    private final EmpresaMetodoPrecificacaoService empresaMetodoPrecificacaoService;
    private final OrcamentoStatusHistoricoService statusHistoricoService;

    public OrcamentoService(OrcamentoRepository repository,
            TenantContextService tenantContextService,
            ClienteService clienteService,
            ConfiguracaoOrcamentoService configuracaoOrcamentoService,
            PrecificacaoService precificacaoService,
            EmpresaMetodoPrecificacaoService empresaMetodoPrecificacaoService,
            OrcamentoStatusHistoricoService statusHistoricoService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
        this.clienteService = clienteService;
        this.configuracaoOrcamentoService = configuracaoOrcamentoService;
        this.precificacaoService = precificacaoService;
        this.empresaMetodoPrecificacaoService = empresaMetodoPrecificacaoService;
        this.statusHistoricoService = statusHistoricoService;
    }

    public java.util.List<OrcamentoResponse> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria())
                .stream().map(OrcamentoResponse::from).toList();
    }

    public OrcamentoResponse buscar(Long id) {
        Orcamento orcamento = buscarEntidade(id);
        return OrcamentoResponse.from(orcamento);
    }

    @Transactional
    public OrcamentoResponse salvar(OrcamentoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        boolean novo = request.getIdOrcamento() == null;
        Orcamento orcamento = novo ? new Orcamento() : buscarEntidade(request.getIdOrcamento());
        aplicarRequest(orcamento, request, idOrganizacao);
        validarObjeto(orcamento);
        clienteService.registrarClienteAPartirDoOrcamento(orcamento);

        BigDecimal totalOrcamento = BigDecimal.ZERO;
        for (OrcamentoItem item : orcamento.getItens()) {
            item.setOrcamento(orcamento);
            validarItem(item, orcamento.getItens());
            BigDecimal totalItem = aplicarMetodoPrecificacao(item, orcamento.getIdEmpresaMetodoPrecificacao());
            item.setVlPrecoTotal(totalItem);
            item.setVlPrecoUnitario(totalItem.divide(item.getQtItem(), 4, java.math.RoundingMode.HALF_UP));
            totalOrcamento = totalOrcamento.add(totalItem);
            if (item.getCamposValor() != null) {
                for (OrcamentoItemCampoValor campo : item.getCamposValor()) {
                    campo.setOrcamentoItem(item);
                }
            }
        }

        if (novo) {
            orcamento.setCdPublico(UUID.randomUUID().toString());
        }
        orcamento.setVlPrecoBase(totalOrcamento);
        orcamento.setVlPrecoFinal(totalOrcamento);

        Orcamento salvo = repository.save(orcamento);
        if (novo) {
            statusHistoricoService.registrar(salvo, null, orcamento.getTpStatus());
        }
        return OrcamentoResponse.from(salvo);
    }

    public BigDecimal previewPrecificacao(OrcamentoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Orcamento orcamento = new Orcamento();
        aplicarRequest(orcamento, request, idOrganizacao);
        if (orcamento.getIdEmpresaMetodoPrecificacao() == null) {
            orcamento.setIdEmpresaMetodoPrecificacao(
                    empresaMetodoPrecificacaoService.obterEmpresaMetodoPrecificacaoSimples().getIdEmpresaMetodoPrecificacao());
        }
        BigDecimal total = BigDecimal.ZERO;
        if (orcamento.getItens() == null) {
            return total;
        }
        for (OrcamentoItem item : orcamento.getItens()) {
            if (item.getIdCatalogo() == null) {
                return total;
            }
            total = total.add(calcularPrecoItem(item, orcamento.getIdEmpresaMetodoPrecificacao()));
        }
        return total;
    }

    @Transactional
    public OrcamentoResponse alterarStatus(Long idOrcamento, StatusOrcamento novoStatus) {
        Orcamento orcamento = buscarEntidade(idOrcamento);
        StatusOrcamento statusAtual = orcamento.getTpStatus();
        validarTransicao(statusAtual, novoStatus);
        orcamento.setTpStatus(novoStatus);
        Orcamento salvo = repository.save(orcamento);
        statusHistoricoService.registrar(salvo, statusAtual, novoStatus);
        return OrcamentoResponse.from(salvo);
    }

    public String sequencia() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        var config = configuracaoOrcamentoService.obterPrimeiroObjeto();
        String sq = SequenciaUtil.gerarSequencia(repository.obterSequencial(idOrganizacao));
        return config.getPrefixoNumero() + "-" + sq;
    }

    @Transactional
    public void excluir(Long id) {
        buscarEntidade(id);
        statusHistoricoService.excluirPorIdOrcamento(id);
        repository.deleteById(id);
    }

    private Orcamento buscarEntidade(Long id) {
        return repository.findByIdOrcamentoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Orcamento nao encontrado"));
    }

    private void aplicarRequest(Orcamento orcamento, OrcamentoRequest request, Long idOrganizacao) {
        orcamento.setIdOrganizacao(idOrganizacao);
        orcamento.setNuOrcamento(request.getNuOrcamento());
        orcamento.setDtEmissao(request.getDtEmissao());
        orcamento.setDtValido(request.getDtValido());
        orcamento.setIdCliente(request.getIdCliente());
        orcamento.setCliente(request.getCliente());
        orcamento.setIdEmpresaMetodoPrecificacao(request.getIdEmpresaMetodoPrecificacao());
        orcamento.setIdCondicaoPagamento(request.getIdCondicaoPagamento());
        orcamento.setNuPrazoEntrega(request.getNuPrazoEntrega() != null ? request.getNuPrazoEntrega() : 20);
        orcamento.setDsObservacoes(request.getDsObservacoes());
        if (request.getTpStatus() != null) {
            orcamento.setTpStatus(request.getTpStatus());
        }
        if (request.getItens() != null) {
            orcamento.setItens(request.getItens());
        }
    }

    private void validarObjeto(Orcamento orcamento) {
        repository.findByNuOrcamentoAndIdOrganizacao(orcamento.getNuOrcamento(), orcamento.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdOrcamento().equals(orcamento.getIdOrcamento())) {
                        throw new ConflictException("Numero de orcamento ja cadastrado");
                    }
                });
        if (orcamento.getItens() == null || orcamento.getItens().isEmpty()) {
            throw new BusinessException("O orcamento deve possuir ao menos um item");
        }
        if (orcamento.getDtValido().isBefore(orcamento.getDtEmissao())) {
            throw new BusinessException("A data de validade nao pode ser anterior a emissao");
        }
    }

    private void validarItem(OrcamentoItem item, java.util.List<OrcamentoItem> itens) {
        boolean repetido = itens.stream()
                .filter(i -> !Objects.equals(i.getIdOrcamentoItem(), item.getIdOrcamentoItem()))
                .anyMatch(i -> i.getIdCatalogo().equals(item.getIdCatalogo()));
        if (repetido) {
            throw new ConflictException("Existem itens repetidos no orcamento");
        }
        if (item.getQtItem() == null || item.getQtItem().signum() <= 0) {
            throw new BusinessException("Quantidade invalida");
        }
        if (item.getVlCustoUnitario() == null) {
            throw new BusinessException("Custo unitario nao informado");
        }
    }

    private BigDecimal aplicarMetodoPrecificacao(OrcamentoItem item, Long idEmpresaMetodoPrecificacao) {
        EmpresaMetodoPrecificacao empresaMetodo = empresaMetodoPrecificacaoService.buscarPorId(idEmpresaMetodoPrecificacao);
        return precificacaoService.precificarItem(item, empresaMetodo);
    }

    private BigDecimal calcularPrecoItem(OrcamentoItem item, Long idEmpresaMetodoPrecificacao) {
        return aplicarMetodoPrecificacao(item, idEmpresaMetodoPrecificacao);
    }

    private void validarTransicao(StatusOrcamento atual, StatusOrcamento novo) {
        boolean valida = (atual == StatusOrcamento.RASCUNHO && novo == StatusOrcamento.GERADO)
                || (atual == StatusOrcamento.GERADO && novo == StatusOrcamento.ENVIADO)
                || (atual == StatusOrcamento.ENVIADO && (novo == StatusOrcamento.APROVADO || novo == StatusOrcamento.REJEITADO));
        if (!valida) {
            throw new BusinessException("Transicao de status invalida: " + atual + " -> " + novo);
        }
    }
}
''')

add("service/VisualizacaoOrcamentoService.java", '''package com.api_orcafacil.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.api_orcafacil.dto.orcamento.ClienteVisualizacaoDTO;
import com.api_orcafacil.dto.orcamento.ItemVisualizacaoDTO;
import com.api_orcafacil.dto.orcamento.MaterialVisualizacaoDTO;
import com.api_orcafacil.dto.orcamento.OrcamentoVisualizacaoDTO;
import com.api_orcafacil.dto.orcamento.StatusHistoricoVisualizacaoDTO;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Cliente;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.model.OrcamentoItem;
import com.api_orcafacil.model.OrcamentoItemCampoValor;
import com.api_orcafacil.model.OrcamentoStatusHistorico;
import com.api_orcafacil.repository.OrcamentoRepository;

@Service
public class VisualizacaoOrcamentoService {

    private final OrcamentoRepository repository;
    private final OrcamentoStatusHistoricoService historicoService;
    private final Optional<NamedParameterJdbcTemplate> centralJdbc;

    public VisualizacaoOrcamentoService(OrcamentoRepository repository,
            OrcamentoStatusHistoricoService historicoService,
            @Qualifier("centralNamedParameterJdbcTemplate") Optional<NamedParameterJdbcTemplate> centralJdbc) {
        this.repository = repository;
        this.historicoService = historicoService;
        this.centralJdbc = centralJdbc;
    }

    public OrcamentoVisualizacaoDTO visualizarPublico(Long idOrcamento, Long idOrganizacao) {
        Orcamento orcamento = repository.findByIdOrcamentoAndIdOrganizacao(idOrcamento, idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Orcamento nao encontrado"));
        OrcamentoVisualizacaoDTO dto = new OrcamentoVisualizacaoDTO();
        mapearCabecalho(orcamento, dto);
        mapearCliente(orcamento, dto);
        mapearItens(orcamento, dto);
        mapearResumo(orcamento, dto);
        mapearHistorico(idOrcamento, dto);
        return dto;
    }

    public OrcamentoVisualizacaoDTO visualizarPorCdPublico(String cdPublico) {
        Orcamento orcamento = repository.findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResourceNotFoundException("Orcamento nao encontrado"));
        return visualizarPublico(orcamento.getIdOrcamento(), orcamento.getIdOrganizacao());
    }

    private void mapearCabecalho(Orcamento orcamento, OrcamentoVisualizacaoDTO dto) {
        dto.setIdOrcamento(orcamento.getIdOrcamento());
        dto.setNuOrcamento(orcamento.getNuOrcamento());
        dto.setDtEmissao(orcamento.getDtEmissao());
        dto.setDtValido(orcamento.getDtValido());
        dto.setStatus(orcamento.getTpStatus());
        dto.setNmEmpresa(buscarNomeOrganizacao(orcamento.getIdOrganizacao()));
        dto.setCondicaoPagamento(orcamento.getNmCondicaoPagamento());
        dto.setNuPrazoEntrega(orcamento.getNuPrazoEntrega());
        dto.setTotalDesconto(new BigDecimal("0.00"));
        dto.setObservacoes(orcamento.getDsObservacoes());
    }

    private String buscarNomeOrganizacao(Long idOrganizacao) {
        return centralJdbc.map(jdbc -> jdbc.queryForObject(
                "select nm_organizacao from organizacao where id_organizacao = :id",
                Map.of("id", idOrganizacao),
                String.class)).orElse(null);
    }

    private void mapearCliente(Orcamento orcamento, OrcamentoVisualizacaoDTO dto) {
        Cliente cliente = orcamento.getCliente();
        if (cliente == null) {
            return;
        }
        ClienteVisualizacaoDTO clienteDto = new ClienteVisualizacaoDTO();
        clienteDto.setIdCliente(cliente.getIdCliente());
        clienteDto.setNome(cliente.getNmCliente());
        clienteDto.setCpfCnpj(cliente.getNuCpfcnpj());
        clienteDto.setEmail(cliente.getDsEmail());
        clienteDto.setTelefone(cliente.getNuTelefone());
        dto.setCliente(clienteDto);
    }

    private void mapearItens(Orcamento orcamento, OrcamentoVisualizacaoDTO dto) {
        List<ItemVisualizacaoDTO> itens = new ArrayList<>();
        for (OrcamentoItem item : orcamento.getItens()) {
            ItemVisualizacaoDTO itemDto = new ItemVisualizacaoDTO();
            itemDto.setIdItem(item.getIdOrcamentoItem());
            itemDto.setCodigo(item.getCdCatalogo());
            itemDto.setDescricao(item.getNmCatalogo());
            itemDto.setQuantidade(item.getQtItem());
            itemDto.setPrecoCusto(item.getVlCustoUnitario());
            itemDto.setPrecoUnitario(item.getVlPrecoUnitario());
            itemDto.setSubtotal(item.getVlPrecoTotal());
            itemDto.setTipo(item.getTpItem());
            itemDto.setMateriais(mapearMateriais(item));
            itens.add(itemDto);
        }
        dto.setItens(itens);
    }

    private List<MaterialVisualizacaoDTO> mapearMateriais(OrcamentoItem item) {
        List<MaterialVisualizacaoDTO> materiais = new ArrayList<>();
        if (item.getCamposValor() == null) {
            return materiais;
        }
        for (OrcamentoItemCampoValor campo : item.getCamposValor()) {
            MaterialVisualizacaoDTO material = new MaterialVisualizacaoDTO();
            material.setNome(campo.getNmCampoPersonalizado());
            material.setDescricao(campo.getDsDescricao());
            material.setValor(campo.getVlInformado());
            material.setTipo(campo.getTpValor());
            materiais.add(material);
        }
        return materiais;
    }

    private void mapearResumo(Orcamento orcamento, OrcamentoVisualizacaoDTO dto) {
        if (orcamento.getEmpresaMetodoPrecificacao() != null) {
            dto.setMetodoPrecificacao(orcamento.getEmpresaMetodoPrecificacao().getNmMetodoPrecificacao());
        }
        dto.setVlPrecoBase(orcamento.getVlPrecoBase());
        dto.setVlPrecoFinal(orcamento.getVlPrecoFinal());
    }

    private void mapearHistorico(Long idOrcamento, OrcamentoVisualizacaoDTO dto) {
        List<StatusHistoricoVisualizacaoDTO> historicoDto = historicoService.listarPorOrcamento(idOrcamento).stream()
                .map(this::mapearHistoricoItem)
                .toList();
        dto.setHistoricoStatus(historicoDto);
    }

    private StatusHistoricoVisualizacaoDTO mapearHistoricoItem(OrcamentoStatusHistorico h) {
        StatusHistoricoVisualizacaoDTO s = new StatusHistoricoVisualizacaoDTO();
        s.setStatusAnterior(h.getTpStatusAnterior());
        s.setStatusAtual(h.getTpStatusNovo());
        s.setDataHora(h.getDtCriacao());
        return s;
    }
}
''')

add("service/AnexoService.java", '''package com.api_orcafacil.service;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class AnexoService {

    private final S3Client s3Client;
    private final String bucket;
    private final String publicBaseUrl;

    public AnexoService(
            @Value("${app.s3.bucket}") String bucket,
            @Value("${app.s3.endpoint}") String endpoint,
            @Value("${app.s3.region}") String region,
            @Value("${app.s3.access-key}") String accessKey,
            @Value("${app.s3.secret-key}") String secretKey) {
        this.bucket = bucket;
        this.publicBaseUrl = endpoint.replace("https://", "https://" + bucket + ".")
                .replace("http://", "http://" + bucket + ".");
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String uploadFotoPerfil(Long idUsuario, MultipartFile file) throws IOException {
        String key = "perfil/" + idUsuario + "/" + file.getOriginalFilename();
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build(), RequestBody.fromBytes(file.getBytes()));
        return publicBaseUrl + "/" + key;
    }

    public void removerFotoPerfil(String fotoUrl) {
        if (fotoUrl == null || fotoUrl.isBlank()) {
            return;
        }
        String key = extrairChave(fotoUrl);
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }

    private String extrairChave(String url) {
        int idx = url.indexOf(bucket);
        if (idx >= 0) {
            return url.substring(url.indexOf('/', idx + bucket.length()) + 1);
        }
        return url.substring(url.lastIndexOf('/') > url.indexOf("://") ? url.indexOf('/', url.indexOf("://") + 3) : 0);
    }
}
''')

add("service/PerfilService.java", '''package com.api_orcafacil.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api_orcafacil.dto.perfil.PerfilRequest;
import com.api_orcafacil.dto.perfil.PerfilResponse;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.security.CentralAuthDirectory;

@Service
public class PerfilService {

    private final TenantContextService tenantContextService;
    private final Optional<NamedParameterJdbcTemplate> centralJdbc;
    private final Optional<CentralAuthDirectory> centralAuthDirectory;
    private final PasswordEncoder passwordEncoder;
    private final AnexoService anexoService;

    public PerfilService(TenantContextService tenantContextService,
            @Qualifier("centralNamedParameterJdbcTemplate") Optional<NamedParameterJdbcTemplate> centralJdbc,
            Optional<CentralAuthDirectory> centralAuthDirectory,
            PasswordEncoder passwordEncoder,
            AnexoService anexoService) {
        this.tenantContextService = tenantContextService;
        this.centralJdbc = centralJdbc;
        this.centralAuthDirectory = centralAuthDirectory;
        this.passwordEncoder = passwordEncoder;
        this.anexoService = anexoService;
    }

    public PerfilResponse obter() {
        Long idUsuario = tenantContextService.idUsuario();
        return centralJdbc.map(jdbc -> jdbc.queryForObject("""
                select id_usuario, nu_cpf, nm_usuario, ds_foto_url
                from usuario_global where id_usuario = :id
                """,
                Map.of("id", idUsuario),
                (rs, rowNum) -> {
                    PerfilResponse p = new PerfilResponse();
                    p.setIdUsuario(rs.getLong("id_usuario"));
                    p.setLogin(rs.getString("nu_cpf"));
                    p.setNome(rs.getString("nm_usuario"));
                    p.setFotoUrl(rs.getString("ds_foto_url"));
                    return p;
                })).orElseThrow(() -> new ResourceNotFoundException("Perfil nao encontrado"));
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void atualizar(PerfilRequest request) {
        Long idUsuario = tenantContextService.idUsuario();
        centralJdbc.ifPresent(jdbc -> {
            if (request.getSenha() != null && !request.getSenha().isBlank()) {
                jdbc.update("""
                        update usuario_global set nm_usuario = :nome, ds_senha = :senha, dt_atualizacao = now()
                        where id_usuario = :id
                        """,
                        Map.of("nome", request.getNome(), "senha", passwordEncoder.encode(request.getSenha()), "id", idUsuario));
            } else {
                jdbc.update("""
                        update usuario_global set nm_usuario = :nome, dt_atualizacao = now()
                        where id_usuario = :id
                        """,
                        Map.of("nome", request.getNome(), "id", idUsuario));
            }
        });
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public String uploadFoto(MultipartFile file) throws Exception {
        Long idUsuario = tenantContextService.idUsuario();
        PerfilResponse atual = obter();
        if (atual.getFotoUrl() != null) {
            anexoService.removerFotoPerfil(atual.getFotoUrl());
        }
        String url = anexoService.uploadFotoPerfil(idUsuario, file);
        centralJdbc.ifPresent(jdbc -> jdbc.update(
                "update usuario_global set ds_foto_url = :url, dt_atualizacao = now() where id_usuario = :id",
                Map.of("url", url, "id", idUsuario)));
        return url;
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void removerFoto() {
        Long idUsuario = tenantContextService.idUsuario();
        PerfilResponse atual = obter();
        anexoService.removerFotoPerfil(atual.getFotoUrl());
        centralJdbc.ifPresent(jdbc -> jdbc.update(
                "update usuario_global set ds_foto_url = null, dt_atualizacao = now() where id_usuario = :id",
                Map.of("id", idUsuario)));
    }
}
''')

add("service/PlanoAssinaturaPlatformService.java", '''package com.api_orcafacil.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.precificacao.PlanoAssinaturaRequest;
import com.api_orcafacil.dto.precificacao.PlanoAssinaturaResponse;
import com.api_orcafacil.exception.ResourceNotFoundException;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager")
public class PlanoAssinaturaPlatformService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PlanoAssinaturaPlatformService(
            @Qualifier("centralNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PlanoAssinaturaResponse> listar() {
        return jdbcTemplate.query("""
                select id_planoassinatura, nm_planoassinatura, vl_mensal, nu_limitemensagens,
                       nu_limiteatendentes, fl_ativo, dt_criacao, dt_atualizacao
                from plano_assinatura order by nm_planoassinatura
                """, Map.of(), this::map);
    }

    public PlanoAssinaturaResponse buscar(Long id) {
        List<PlanoAssinaturaResponse> lista = jdbcTemplate.query("""
                select id_planoassinatura, nm_planoassinatura, vl_mensal, nu_limitemensagens,
                       nu_limiteatendentes, fl_ativo, dt_criacao, dt_atualizacao
                from plano_assinatura where id_planoassinatura = :id
                """, Map.of("id", id), this::map);
        if (lista.isEmpty()) {
            throw new ResourceNotFoundException("Plano nao encontrado");
        }
        return lista.getFirst();
    }

    public PlanoAssinaturaResponse criar(PlanoAssinaturaRequest request) {
        return jdbcTemplate.queryForObject("""
                insert into plano_assinatura (nm_planoassinatura, vl_mensal, nu_limitemensagens, nu_limiteatendentes, fl_ativo)
                values (:nome, :valor, :limiteMsg, :limiteAtend, coalesce(:ativo, true))
                returning id_planoassinatura, nm_planoassinatura, vl_mensal, nu_limitemensagens,
                          nu_limiteatendentes, fl_ativo, dt_criacao, dt_atualizacao
                """,
                Map.of(
                        "nome", request.getNmPlanoAssinatura(),
                        "valor", request.getVlMensal(),
                        "limiteMsg", request.getNuLimiteMensagens() != null ? request.getNuLimiteMensagens() : 0,
                        "limiteAtend", request.getNuLimiteAtendentes() != null ? request.getNuLimiteAtendentes() : 0,
                        "ativo", request.getFlAtivo()),
                this::map);
    }

    public PlanoAssinaturaResponse atualizar(Long id, PlanoAssinaturaRequest request) {
        buscar(id);
        return jdbcTemplate.queryForObject("""
                update plano_assinatura set
                    nm_planoassinatura = :nome,
                    vl_mensal = :valor,
                    nu_limitemensagens = :limiteMsg,
                    nu_limiteatendentes = :limiteAtend,
                    fl_ativo = coalesce(:ativo, fl_ativo),
                    dt_atualizacao = now()
                where id_planoassinatura = :id
                returning id_planoassinatura, nm_planoassinatura, vl_mensal, nu_limitemensagens,
                          nu_limiteatendentes, fl_ativo, dt_criacao, dt_atualizacao
                """,
                Map.of(
                        "id", id,
                        "nome", request.getNmPlanoAssinatura(),
                        "valor", request.getVlMensal(),
                        "limiteMsg", request.getNuLimiteMensagens(),
                        "limiteAtend", request.getNuLimiteAtendentes(),
                        "ativo", request.getFlAtivo()),
                this::map);
    }

    public void excluir(Long id) {
        buscar(id);
        jdbcTemplate.update("delete from plano_assinatura where id_planoassinatura = :id", Map.of("id", id));
    }

    private PlanoAssinaturaResponse map(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        PlanoAssinaturaResponse r = new PlanoAssinaturaResponse();
        r.setIdPlanoAssinatura(rs.getLong("id_planoassinatura"));
        r.setNmPlanoAssinatura(rs.getString("nm_planoassinatura"));
        r.setVlMensal(rs.getObject("vl_mensal") != null ? rs.getDouble("vl_mensal") : null);
        r.setNuLimiteMensagens(rs.getInt("nu_limitemensagens"));
        r.setNuLimiteAtendentes(rs.getInt("nu_limiteatendentes"));
        r.setFlAtivo(rs.getBoolean("fl_ativo"));
        r.setDtCriacao(rs.getObject("dt_criacao", java.time.LocalDateTime.class));
        r.setDtAtualizacao(rs.getObject("dt_atualizacao", java.time.LocalDateTime.class));
        return r;
    }
}
''')

add("relatorio/JasperService.java", '''package com.api_orcafacil.relatorio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingFunction;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
public class JasperService {

    private static final String REPORT_FOLDER = "/report/";
    private final DataSource dataSource;

    public JasperService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public byte[] exportarGenerico(String templateNome, String formato, Map<String, Object> parametros) throws Exception {
        String caminho = REPORT_FOLDER + templateNome + ".jrxml";
        InputStream jrxmlStream = getClass().getResourceAsStream(caminho);
        if (jrxmlStream == null) {
            throw new RuntimeException("Relatorio nao encontrado: " + templateNome);
        }
        parametros.put("REPORT_STYLE", carregarReportComponent(REPORT_FOLDER + "style/RelatorioStyles.jrtx"));
        parametros.put("ICON_PATH", carregarReportComponent(REPORT_FOLDER + "icone.png"));
        try (Connection connection = dataSource.getConnection()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);
            return switch (formato.toUpperCase()) {
                case "PDF" -> JasperExportManager.exportReportToPdf(jasperPrint);
                case "XLSX" -> {
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                    exporter.exportReport();
                    yield outputStream.toByteArray();
                }
                default -> throw new IllegalArgumentException("Formato nao suportado: " + formato);
            };
        }
    }

    public String carregarReportComponent(String componentPath) throws Exception {
        InputStream componentStream = getClass().getResourceAsStream(componentPath);
        if (componentStream == null) {
            throw new RuntimeException("Componente do relatorio nao encontrado: " + componentPath);
        }
        String extension = componentPath.endsWith(".jrtx") ? ".jrtx" : ".jrxml";
        File tempComponent = File.createTempFile("REPORT_COMPONENT_", extension);
        try (FileOutputStream out = new FileOutputStream(tempComponent)) {
            componentStream.transferTo(out);
        }
        return tempComponent.getAbsolutePath();
    }

    public <T extends RelatorioRequestBase> ResponseEntity<?> gerarRelatorio(T filtro, ThrowingFunction<T, byte[]> gerador,
            String nomeArquivo) throws Exception {
        return gerarRelatorio(filtro, gerador, nomeArquivo, true);
    }

    public <T extends RelatorioRequestBase> ResponseEntity<?> gerarRelatorio(T filtro, ThrowingFunction<T, byte[]> gerador,
            String nomeArquivo, Boolean validar) throws Exception {
        if (Boolean.TRUE.equals(validar)) {
            validarFiltro(filtro);
        }
        byte[] relatorio = gerador.apply(filtro);
        String formato = filtro.getFormatoSaida();
        MediaType mediaType = determinarMediaType(formato);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "." + formato.toLowerCase() + "\"")
                .contentType(mediaType)
                .body(relatorio);
    }

    public <T extends RelatorioRequestBase> void validarFiltro(T filtro) {
        if (filtro.getFormatoSaida() == null || filtro.getFormatoSaida().isBlank()) {
            throw new IllegalArgumentException("O formato de saida e obrigatorio");
        }
    }

    private MediaType determinarMediaType(String formato) {
        return switch (formato.toUpperCase()) {
            case "PDF" -> MediaType.APPLICATION_PDF;
            case "XLSX" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
''')

add("relatorio/RelatorioOrcamentoService.java", '''package com.api_orcafacil.relatorio;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.dto.orcamento.OrcamentoVisualizacaoDTO;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.repository.OrcamentoRepository;
import com.api_orcafacil.service.VisualizacaoOrcamentoService;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class RelatorioOrcamentoService {

    private final VisualizacaoOrcamentoService visualizacaoOrcamento;
    private final OrcamentoRepository repository;

    public RelatorioOrcamentoService(VisualizacaoOrcamentoService visualizacaoOrcamento, OrcamentoRepository repository) {
        this.visualizacaoOrcamento = visualizacaoOrcamento;
        this.repository = repository;
    }

    public byte[] gerarRelatorioOrcamento(String cdPublico) throws Exception {
        Orcamento orcamento = repository.findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orcamento nao encontrado"));
        if (orcamento.getTpStatus() == StatusOrcamento.RASCUNHO) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        OrcamentoVisualizacaoDTO dto = visualizacaoOrcamento.visualizarPublico(
                orcamento.getIdOrcamento(), orcamento.getIdOrganizacao());
        InputStream reportStream = getClass().getResourceAsStream("/orcaReport/orca.jrxml");
        if (reportStream == null) {
            throw new RuntimeException("Arquivo JRXML nao encontrado em /orcaReport/orca.jrxml");
        }
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        Map<String, Object> params = new HashMap<>();
        params.put("TITULO_HEADER", "");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params,
                new JRBeanCollectionDataSource(List.of(dto)));
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
''')

if __name__ == "__main__":
    for rel, content in FILES.items():
        (ROOT / rel).parent.mkdir(parents=True, exist_ok=True)
        (ROOT / rel).write_text(content, encoding="utf-8")
        print(f"Wrote {rel}")
    print(f"Total: {len(FILES)}")
