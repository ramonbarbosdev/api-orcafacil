package com.api_orcafacil.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.api_orcafacil.common.ChaveLimite;
import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.dto.orcamento.OrcamentoPreviewPrecificacaoRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoMensagemCompartilhamentoResponse;
import com.api_orcafacil.dto.orcamento.OrcamentoNotificacaoHistoricoResponse;
import com.api_orcafacil.dto.orcamento.OrcamentoEnviarRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoEnviarResponse;
import com.api_orcafacil.dto.orcamento.OrcamentoItemCampoValorRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoItemRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoResponse;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.model.OrcamentoItem;
import com.api_orcafacil.model.OrcamentoItemCampoValor;
import com.api_orcafacil.model.OrcamentoNotificacaoEnviada;
import com.api_orcafacil.notificacao.service.OrcamentoNotificacaoHistoricoService;
import com.api_orcafacil.notificacao.service.OrcamentoNotificacaoService;
import com.api_orcafacil.notificacao.service.NotificacaoOrganizacaoResolver;
import com.api_orcafacil.repository.OrcamentoNotificacaoEnviadaRepository;
import com.api_orcafacil.repository.CatalogoRepository;
import com.api_orcafacil.repository.CondicaoPagamentoRepository;
import com.api_orcafacil.repository.OrcamentoRepository;

@Service
public class OrcamentoService {

    private static final Logger log = LoggerFactory.getLogger(OrcamentoService.class);

    private final OrcamentoRepository repository;
    private final CatalogoRepository catalogoRepository;
    private final CondicaoPagamentoRepository condicaoPagamentoRepository;
    private final TenantContextService tenantContextService;
    private final ClienteService clienteService;
    private final ConfiguracaoOrcamentoService configuracaoOrcamentoService;
    private final PrecificacaoService precificacaoService;
    private final EmpresaMetodoPrecificacaoService empresaMetodoPrecificacaoService;
    private final OrcamentoStatusHistoricoService statusHistoricoService;
    private final ObjectProvider<PoliticaPlanoService> politicaPlanoService;
    private final ObjectProvider<OrcamentoNotificacaoService> orcamentoNotificacaoService;
    private final ObjectProvider<NotificacaoOrganizacaoResolver> notificacaoOrganizacaoResolver;
    private final ObjectProvider<OrcamentoNotificacaoHistoricoService> orcamentoNotificacaoHistoricoService;
    private final ObjectProvider<OrcamentoNotificacaoEnviadaRepository> orcamentoNotificacaoRepository;
    private final ObjectProvider<OrcamentoCentralSyncService> orcamentoCentralSyncService;
    private final ObjectProvider<OrcamentoCentralSyncRetryService> orcamentoCentralSyncRetryService;

    public OrcamentoService(OrcamentoRepository repository,
            CatalogoRepository catalogoRepository,
            CondicaoPagamentoRepository condicaoPagamentoRepository,
            TenantContextService tenantContextService,
            ClienteService clienteService,
            ConfiguracaoOrcamentoService configuracaoOrcamentoService,
            PrecificacaoService precificacaoService,
            EmpresaMetodoPrecificacaoService empresaMetodoPrecificacaoService,
            OrcamentoStatusHistoricoService statusHistoricoService,
            ObjectProvider<PoliticaPlanoService> politicaPlanoService,
            ObjectProvider<OrcamentoNotificacaoService> orcamentoNotificacaoService,
            ObjectProvider<NotificacaoOrganizacaoResolver> notificacaoOrganizacaoResolver,
            ObjectProvider<OrcamentoNotificacaoHistoricoService> orcamentoNotificacaoHistoricoService,
            ObjectProvider<OrcamentoNotificacaoEnviadaRepository> orcamentoNotificacaoRepository,
            ObjectProvider<OrcamentoCentralSyncService> orcamentoCentralSyncService,
            ObjectProvider<OrcamentoCentralSyncRetryService> orcamentoCentralSyncRetryService) {
        this.repository = repository;
        this.catalogoRepository = catalogoRepository;
        this.condicaoPagamentoRepository = condicaoPagamentoRepository;
        this.tenantContextService = tenantContextService;
        this.clienteService = clienteService;
        this.configuracaoOrcamentoService = configuracaoOrcamentoService;
        this.precificacaoService = precificacaoService;
        this.empresaMetodoPrecificacaoService = empresaMetodoPrecificacaoService;
        this.statusHistoricoService = statusHistoricoService;
        this.politicaPlanoService = politicaPlanoService;
        this.orcamentoNotificacaoService = orcamentoNotificacaoService;
        this.notificacaoOrganizacaoResolver = notificacaoOrganizacaoResolver;
        this.orcamentoNotificacaoHistoricoService = orcamentoNotificacaoHistoricoService;
        this.orcamentoNotificacaoRepository = orcamentoNotificacaoRepository;
        this.orcamentoCentralSyncService = orcamentoCentralSyncService;
        this.orcamentoCentralSyncRetryService = orcamentoCentralSyncRetryService;
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
        Long idCliente = clienteService.registrarClienteAPartirDoOrcamento(request.getCliente());
        orcamento.setIdCliente(idCliente);
        limparAssociacoesSomenteLeitura(orcamento);
        orcamento.setTpStatus(StatusOrcamento.GERADO);
        prepararItensAntesDeConsultas(orcamento);
        validarReferencias(orcamento, idOrganizacao);
        if (orcamento.getIdEmpresaMetodoPrecificacao() == null) {
            orcamento.setIdEmpresaMetodoPrecificacao(
                    empresaMetodoPrecificacaoService.obterEmpresaMetodoPrecificacaoSimples().getIdEmpresaMetodoPrecificacao());
        }

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

        validarObjeto(orcamento);

        if (novo) {
            orcamento.setCdPublico(UUID.randomUUID().toString());
        }
        orcamento.setVlPrecoBase(totalOrcamento);
        orcamento.setVlPrecoFinal(totalOrcamento);
        limparAssociacoesSomenteLeitura(orcamento);

        Orcamento salvo = repository.save(orcamento);
        if (novo) {
            statusHistoricoService.registrar(salvo, null, StatusOrcamento.GERADO);
        }

        OrcamentoResponse response = OrcamentoResponse.from(salvo);
        agendarEfeitosCentraisAposCommit(salvo, novo);
        return response;
    }

    @Transactional(readOnly = true)
    public BigDecimal previewPrecificacao(OrcamentoPreviewPrecificacaoRequest request) {
        Long idEmpresaMetodoPrecificacao = request.getIdEmpresaMetodoPrecificacao();
        if (idEmpresaMetodoPrecificacao == null) {
            idEmpresaMetodoPrecificacao = empresaMetodoPrecificacaoService
                    .obterEmpresaMetodoPrecificacaoSimples()
                    .getIdEmpresaMetodoPrecificacao();
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OrcamentoItemRequest itemRequest : request.getItens()) {
            OrcamentoItem item = itemRequest.toEntity();
            if (item.getIdCatalogo() == null) {
                throw new BusinessException("Catalogo do item nao informado");
            }
            total = total.add(calcularPrecoItem(item, idEmpresaMetodoPrecificacao));
        }
        return total;
    }

    @Transactional(rollbackFor = Exception.class)
    public OrcamentoResponse gerar(Long idOrcamento, OrcamentoRequest request) {
        request.setIdOrcamento(idOrcamento);
        return salvar(request);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrcamentoEnviarResponse enviarComNotificacao(Long idOrcamento, OrcamentoEnviarRequest request) {
        Orcamento entidade = buscarEntidade(idOrcamento);
        OrcamentoResponse orcamento = OrcamentoResponse.from(entidade);
        boolean integracaoAtiva = integracaoNotificacaoAtiva(tenantContextService.idOrganizacaoObrigatoria());

        OrcamentoNotificacaoService notificacaoService = orcamentoNotificacaoService.getIfAvailable();
        String mensagemCompartilhamento = notificacaoService != null
                ? notificacaoService.resolverMensagemPreview(entidade, request != null ? request.getMensagem() : null)
                : null;

        List<OrcamentoEnviarResponse.ResultadoNotificacao> notificacoes =
                integracaoAtiva && notificacaoService != null
                        ? notificacaoService.notificarOrcamentoEnviado(entidade, request)
                        : List.of();

        if (integracaoAtiva && notificacaoService != null) {
            String mensagemEnviada = mensagemCompartilhamento;
            orcamentoNotificacaoHistoricoService.ifAvailable(historico -> {
                for (OrcamentoEnviarResponse.ResultadoNotificacao resultado : notificacoes) {
                    historico.registrar(idOrcamento, resultado, mensagemEnviada);
                }
            });
        }

        return new OrcamentoEnviarResponse(orcamento, notificacoes, integracaoAtiva, mensagemCompartilhamento);
    }

    @Transactional(readOnly = true)
    public OrcamentoMensagemCompartilhamentoResponse previewMensagemCompartilhamento(Long idOrcamento) {
        Orcamento entidade = buscarEntidade(idOrcamento);
        OrcamentoNotificacaoService notificacaoService = orcamentoNotificacaoService.getIfAvailable();
        if (notificacaoService == null) {
            throw new BusinessException("Integracao de notificacoes desabilitada");
        }
        OrcamentoMensagemCompartilhamentoResponse resposta =
                notificacaoService.previewMensagemCompartilhamento(entidade);
        resposta.setIntegracaoNotificacaoAtiva(integracaoNotificacaoAtiva(tenantContextService.idOrganizacaoObrigatoria()));
        return resposta;
    }

    private boolean integracaoNotificacaoAtiva(Long idOrganizacao) {
        NotificacaoOrganizacaoResolver resolver = notificacaoOrganizacaoResolver.getIfAvailable();
        if (resolver == null) {
            return false;
        }
        Long orgId = idOrganizacao != null ? idOrganizacao : tenantContextService.idOrganizacaoObrigatoria();
        return resolver.integracaoConfigurada(orgId);
    }

    @Transactional(readOnly = true)
    public List<OrcamentoNotificacaoHistoricoResponse> listarNotificacoes(Long idOrcamento) {
        buscarEntidade(idOrcamento);
        OrcamentoNotificacaoEnviadaRepository repo = orcamentoNotificacaoRepository.getIfAvailable();
        if (repo == null) {
            return List.of();
        }
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        return repo.findByIdOrcamentoAndIdOrganizacaoOrderByDtCriacaoDesc(idOrcamento, idOrganizacao)
                .stream()
                .map(this::paraHistoricoResponse)
                .collect(Collectors.toList());
    }

    private OrcamentoNotificacaoHistoricoResponse paraHistoricoResponse(OrcamentoNotificacaoEnviada item) {
        return new OrcamentoNotificacaoHistoricoResponse(
                item.getIdOrcamentoNotificacao(),
                item.getTpCanal(),
                item.getDsDestinatario(),
                item.isFlSucesso(),
                item.getIdNotificacaoExterna(),
                item.getDsErro(),
                item.getDsMensagem(),
                item.getDtCriacao());
    }
    

    /** Reservado para fluxo futuro de aprovacao/rejeicao. */
    @Transactional(rollbackFor = Exception.class)
    public OrcamentoResponse alterarStatus(Long idOrcamento, StatusOrcamento novoStatus) {
        Orcamento orcamento = buscarEntidade(idOrcamento);
        StatusOrcamento statusAtual = orcamento.getTpStatus();
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
        String cdPublico = orcamento.getCdPublico();
        Long idOrganizacao = orcamento.getIdOrganizacao();
        boolean consumoMesAtual = YearMonth.from(orcamento.getDtCriacao()).equals(YearMonth.now());

        statusHistoricoService.excluirPorIdOrcamento(id);
        repository.deleteById(id);

        agendarLimpezaCentraisAposCommit(cdPublico, idOrganizacao, id, consumoMesAtual);
    }

    private void agendarEfeitosCentraisAposCommit(Orcamento salvo, boolean novo) {
        if (!novo && (salvo.getCdPublico() == null || salvo.getCdPublico().isBlank())) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            aplicarEfeitosCentrais(salvo, novo);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                aplicarEfeitosCentrais(salvo, novo);
            }
        });
    }

    private void aplicarEfeitosCentrais(Orcamento salvo, boolean novo) {
        orcamentoCentralSyncService.ifAvailable(sync -> {
            try {
                sync.aplicarPosCommitSalvar(
                        salvo.getIdOrganizacao(), salvo.getCdPublico(), salvo.getIdOrcamento(), novo);
            } catch (Exception ex) {
                log.error(
                        "Falha ao sincronizar orcamento {} com banco central apos commit do tenant",
                        salvo.getIdOrcamento(),
                        ex);
                orcamentoCentralSyncRetryService.ifAvailable(retry -> retry.agendarSalvar(
                        salvo.getIdOrganizacao(), salvo.getCdPublico(), salvo.getIdOrcamento(), novo));
            }
        });
    }

    private void agendarLimpezaCentraisAposCommit(
            String cdPublico, Long idOrganizacao, Long idOrcamento, boolean consumoMesAtual) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            aplicarLimpezaCentrais(cdPublico, idOrganizacao, idOrcamento, consumoMesAtual);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                aplicarLimpezaCentrais(cdPublico, idOrganizacao, idOrcamento, consumoMesAtual);
            }
        });
    }

    private void aplicarLimpezaCentrais(
            String cdPublico, Long idOrganizacao, Long idOrcamento, boolean consumoMesAtual) {
        orcamentoCentralSyncService.ifAvailable(sync -> {
            try {
                sync.aplicarPosCommitExcluir(cdPublico, idOrganizacao, idOrcamento, consumoMesAtual);
            } catch (Exception ex) {
                log.error(
                        "Falha ao limpar dados centrais do orcamento {} apos exclusao no tenant",
                        idOrcamento,
                        ex);
                orcamentoCentralSyncRetryService.ifAvailable(retry -> retry.agendarExcluir(
                        cdPublico, idOrganizacao, idOrcamento, consumoMesAtual));
            }
        });
    }


    private Orcamento buscarEntidade(Long id) {
        Orcamento orcamento = repository.findByIdOrcamentoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Orcamento nao encontrado"));
        inicializarItensGerenciados(orcamento);
        return orcamento;
    }

    private void inicializarItensGerenciados(Orcamento orcamento) {
        List<OrcamentoItem> itens = orcamento.getItens();
        if (itens == null) {
            return;
        }
        for (OrcamentoItem item : itens) {
            List<OrcamentoItemCampoValor> campos = item.getCamposValor();
            if (campos != null) {
                campos.size();
            }
        }
    }

    private void prepararItensAntesDeConsultas(Orcamento orcamento) {
        if (orcamento.getItens() == null) {
            return;
        }
        for (OrcamentoItem item : orcamento.getItens()) {
            item.setOrcamento(orcamento);
            if (item.getVlPrecoUnitario() == null) {
                item.setVlPrecoUnitario(BigDecimal.ZERO);
            }
            if (item.getVlPrecoTotal() == null) {
                if (item.getQtItem() != null) {
                    item.setVlPrecoTotal(item.getVlPrecoUnitario().multiply(item.getQtItem()));
                } else {
                    item.setVlPrecoTotal(BigDecimal.ZERO);
                }
            }
        }
    }

    private void aplicarRequest(Orcamento orcamento, OrcamentoRequest request, Long idOrganizacao) {
        orcamento.setIdOrganizacao(idOrganizacao);
        orcamento.setNuOrcamento(request.getNuOrcamento());
        orcamento.setDtEmissao(request.getDtEmissao());
        orcamento.setDtValido(request.getDtValido());
        orcamento.setIdEmpresaMetodoPrecificacao(request.getIdEmpresaMetodoPrecificacao());
        orcamento.setIdCondicaoPagamento(request.getIdCondicaoPagamento());
        orcamento.setNuPrazoEntrega(request.getNuPrazoEntrega() != null ? request.getNuPrazoEntrega() : 20);
        orcamento.setDsObservacoes(request.getDsObservacoes());
        if (request.getItens() != null) {
            if (orcamento.getIdOrcamento() == null) {
                List<OrcamentoItem> itens = request.getItens().stream()
                        .map(OrcamentoItemRequest::toEntity)
                        .collect(Collectors.toCollection(ArrayList::new));
                for (OrcamentoItem item : itens) {
                    item.setOrcamento(orcamento);
                }
                orcamento.setItens(itens);
            } else {
                sincronizarItens(orcamento, request.getItens());
            }
        }
    }

    private void sincronizarItens(Orcamento orcamento, List<OrcamentoItemRequest> requests) {
        List<OrcamentoItem> itens = orcamento.getItens();
        if (itens == null) {
            itens = new ArrayList<>();
            orcamento.setItens(itens);
        }

        Set<Long> idsRequest = new HashSet<>();
        for (OrcamentoItemRequest request : requests) {
            if (request.getIdOrcamentoItem() != null) {
                idsRequest.add(request.getIdOrcamentoItem());
            }
        }
        itens.removeIf(item -> item.getIdOrcamentoItem() != null
                && !idsRequest.contains(item.getIdOrcamentoItem()));

        for (OrcamentoItemRequest request : requests) {
            if (request.getIdOrcamentoItem() != null) {
                OrcamentoItem existente = itens.stream()
                        .filter(item -> request.getIdOrcamentoItem().equals(item.getIdOrcamentoItem()))
                        .findFirst()
                        .orElse(null);
                if (existente != null) {
                    aplicarItemRequest(existente, request);
                    continue;
                }
            }
            itens.add(request.toEntity());
        }
        for (OrcamentoItem item : itens) {
            item.setOrcamento(orcamento);
        }
    }

    private void aplicarItemRequest(OrcamentoItem item, OrcamentoItemRequest request) {
        item.setIdCatalogo(request.getIdCatalogo());
        item.setQtItem(request.getQtItem());
        item.setVlCustoUnitario(request.getVlCustoUnitario());
        BigDecimal precoUnitario = request.getVlPrecoUnitario() != null
                ? request.getVlPrecoUnitario()
                : BigDecimal.ZERO;
        item.setVlPrecoUnitario(precoUnitario);
        item.setVlPrecoTotal(
                request.getQtItem() != null ? precoUnitario.multiply(request.getQtItem()) : BigDecimal.ZERO);
        sincronizarCamposValor(item, request.getCamposValor());
    }

    private void sincronizarCamposValor(OrcamentoItem item, List<OrcamentoItemCampoValorRequest> camposRequest) {
        List<OrcamentoItemCampoValor> campos = item.getCamposValor();
        if (campos == null) {
            campos = new ArrayList<>();
            item.setCamposValor(campos);
        }
        if (camposRequest == null || camposRequest.isEmpty()) {
            campos.clear();
            return;
        }

        Set<Long> idsRequest = new HashSet<>();
        for (OrcamentoItemCampoValorRequest campo : camposRequest) {
            if (campo.getIdOrcamentoItemCampoValor() != null) {
                idsRequest.add(campo.getIdOrcamentoItemCampoValor());
            }
        }
        campos.removeIf(campo -> campo.getIdOrcamentoItemCampoValor() != null
                && !idsRequest.contains(campo.getIdOrcamentoItemCampoValor()));

        for (OrcamentoItemCampoValorRequest campoRequest : camposRequest) {
            if (campoRequest.getIdOrcamentoItemCampoValor() != null) {
                campos.stream()
                        .filter(campo -> campoRequest.getIdOrcamentoItemCampoValor()
                                .equals(campo.getIdOrcamentoItemCampoValor()))
                        .findFirst()
                        .ifPresent(campo -> copiarCamposValor(campo, campoRequest));
            } else {
                OrcamentoItemCampoValor novo = new OrcamentoItemCampoValor();
                copiarCamposValor(novo, campoRequest);
                novo.setOrcamentoItem(item);
                campos.add(novo);
            }
        }
    }

    private void copiarCamposValor(OrcamentoItemCampoValor destino, OrcamentoItemCampoValorRequest origem) {
        destino.setIdCampoPersonalizado(origem.getIdCampoPersonalizado());
        destino.setTpValor(origem.getTpValor());
        destino.setVlInformado(origem.getVlInformado());
        destino.setDsDescricao(origem.getDsDescricao());
        destino.setCampoPersonalizado(null);
    }

    private void limparAssociacoesSomenteLeitura(Orcamento orcamento) {
        orcamento.setCliente(null);
        orcamento.setCondicaoPagamento(null);
        orcamento.setEmpresaMetodoPrecificacao(null);
        if (orcamento.getItens() == null) {
            return;
        }
        for (OrcamentoItem item : orcamento.getItens()) {
            item.setCatalogo(null);
            if (item.getCamposValor() == null) {
                continue;
            }
            for (OrcamentoItemCampoValor campo : item.getCamposValor()) {
                campo.setCampoPersonalizado(null);
            }
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
        if (orcamento.getDtEmissao() == null || orcamento.getDtValido() == null) {
            throw new BusinessException("Datas de emissao e validade sao obrigatorias");
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
                .filter(i -> i != item)
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

    private void validarReferencias(Orcamento orcamento, Long idOrganizacao) {
        condicaoPagamentoRepository.findByIdCondicaoPagamentoAndIdOrganizacao(
                        orcamento.getIdCondicaoPagamento(), idOrganizacao)
                .orElseThrow(() -> new BusinessException("Condicao de pagamento nao encontrada"));
        if (orcamento.getIdEmpresaMetodoPrecificacao() != null) {
            empresaMetodoPrecificacaoService.buscarEntidadePorId(orcamento.getIdEmpresaMetodoPrecificacao());
        }
        if (orcamento.getItens() != null) {
            for (OrcamentoItem item : orcamento.getItens()) {
                if (item.getIdCatalogo() != null) {
                    catalogoRepository.findByIdCatalogoAndIdOrganizacao(item.getIdCatalogo(), idOrganizacao)
                            .orElseThrow(() -> new BusinessException(
                                    "Catalogo do item nao encontrado: " + item.getIdCatalogo()));
                }
            }
        }
    }
}
