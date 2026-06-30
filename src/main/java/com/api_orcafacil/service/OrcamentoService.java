package com.api_orcafacil.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.ChaveLimite;
import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.dto.orcamento.OrcamentoRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoItemRequest;
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
    private final ObjectProvider<PoliticaPlanoService> politicaPlanoService;

    public OrcamentoService(OrcamentoRepository repository,
            TenantContextService tenantContextService,
            ClienteService clienteService,
            ConfiguracaoOrcamentoService configuracaoOrcamentoService,
            PrecificacaoService precificacaoService,
            EmpresaMetodoPrecificacaoService empresaMetodoPrecificacaoService,
            OrcamentoStatusHistoricoService statusHistoricoService,
            ObjectProvider<PoliticaPlanoService> politicaPlanoService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
        this.clienteService = clienteService;
        this.configuracaoOrcamentoService = configuracaoOrcamentoService;
        this.precificacaoService = precificacaoService;
        this.empresaMetodoPrecificacaoService = empresaMetodoPrecificacaoService;
        this.statusHistoricoService = statusHistoricoService;
        this.politicaPlanoService = politicaPlanoService;
    }

    @Transactional(readOnly = true)
    public java.util.List<OrcamentoResponse> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria())
                .stream().map(OrcamentoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrcamentoResponse buscar(Long id) {
        Orcamento orcamento = buscarEntidade(id);
        return OrcamentoResponse.from(orcamento);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrcamentoResponse salvar(OrcamentoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        boolean novo = request.getIdOrcamento() == null;
        if (novo) {
            politicaPlanoService.ifAvailable(p -> p.validarLimiteNovoRegistroAtual(ChaveLimite.ORCAMENTOS_MES));
        }
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

        OrcamentoResponse response = OrcamentoResponse.from(salvo);
        if (novo) {
            politicaPlanoService.ifAvailable(
                    p -> p.registrarConsumoAtual(ChaveLimite.ORCAMENTOS_MES, 1));
        }

        return response;
    }

    @Transactional(readOnly = true)
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

    @Transactional(rollbackFor = Exception.class)
    public OrcamentoResponse alterarStatus(Long idOrcamento, StatusOrcamento novoStatus) {
        Orcamento orcamento = buscarEntidade(idOrcamento);
        StatusOrcamento statusAtual = orcamento.getTpStatus();
        validarTransicao(statusAtual, novoStatus);
        orcamento.setTpStatus(novoStatus);
        Orcamento salvo = repository.save(orcamento);
        statusHistoricoService.registrar(salvo, statusAtual, novoStatus);
        return OrcamentoResponse.from(salvo);
    }

    @Transactional(readOnly = true)
    public String sequencia() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        var config = configuracaoOrcamentoService.obterPrimeiroObjeto();
        String sq = SequenciaUtil.gerarSequencia(repository.obterSequencial(idOrganizacao));
        return config.getPrefixoNumero() + "-" + sq;
    }

    @Transactional(rollbackFor = Exception.class)
    public void excluir(Long id) {
        Orcamento orcamento = buscarEntidade(id);
        boolean consumoMesAtual = YearMonth.from(orcamento.getDtCriacao()).equals(YearMonth.now());
        statusHistoricoService.excluirPorIdOrcamento(id);
        repository.deleteById(id);
        if (consumoMesAtual) {
            politicaPlanoService.ifAvailable(
                    p -> p.registrarConsumoAtual(ChaveLimite.ORCAMENTOS_MES, -1));
        }
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
            orcamento.setItens(request.getItens().stream()
                    .map(OrcamentoItemRequest::toEntity)
                    .toList());
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
        if (item.getIdCatalogo() == null) {
            throw new BusinessException("Catalogo do item nao informado");
        }
        boolean repetido = itens.stream()
                .filter(i -> !Objects.equals(i.getIdOrcamentoItem(), item.getIdOrcamentoItem()))
                .filter(i -> i.getIdCatalogo() != null)
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
        EmpresaMetodoPrecificacao empresaMetodo = empresaMetodoPrecificacaoService.buscarEntidadePorId(idEmpresaMetodoPrecificacao);
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
