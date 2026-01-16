package com.api_orcafacil.domain.orcamento.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.cliente.model.Cliente;
import com.api_orcafacil.domain.cliente.service.ClienteService;
import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.repository.EmpresaRepository;
import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItem;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItemCampoValor;
import com.api_orcafacil.domain.orcamento.repository.CondicaoPagamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.ConfiguracaoOrcamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoItemRepository;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoRepository;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.service.EmpresaMetodoPrecificacaoService;
import com.api_orcafacil.domain.precificacao.service.PrecificacaoService;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.servico.repository.ServicoRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.enums.StatusOrcamento;
import com.api_orcafacil.util.MestreDetalheUtils;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository repository;

    @Autowired
    private ValidacaoService validacaoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ConfiguracaoOrcamentoService configuracaoOrcamentoService;

    @Autowired
    private OrcamentoItemService orcamentoItemService;

    @Autowired
    private OrcamentoItemCampoValorService orcamentoItemCampoValorService;

    @Autowired
    private PrecificacaoService precificacaoService;

    @Autowired
    private EmpresaMetodoPrecificacaoService empresaMetodoPrecificacaoService;

    @Autowired
    private OrcamentoStatusHistoricoService statusHistoricoService;

    public static final Function<Orcamento, Long> ID_FUNCTION = Orcamento::getIdOrcamento;

    public static final Function<Orcamento, String> SEQUENCIA_FUNCTION = Orcamento::getNuOrcamento;

    @Transactional
    public Orcamento salvar(Orcamento objeto) throws Exception {

        // TO:DO - rever calculo com metodo de precificacao

        validarObjeto(objeto);

        Cliente clientePersistido = clienteService.registrarClienteAPartirDoOrcamento(objeto);

        objeto.setCliente(clientePersistido);

        BigDecimal totalOrcamento = BigDecimal.ZERO;

        for (OrcamentoItem item : objeto.getOrcamentoItem()) {
            item.setOrcamento(objeto);
            orcamentoItemService.validarObjeto(item, objeto.getOrcamentoItem());

            BigDecimal totalItem = aplicarMetodoPrecificacao(item, objeto.getIdEmpresaMetodoPrecificacao());
            totalOrcamento = totalOrcamento.add(totalItem);

            for (OrcamentoItemCampoValor campo : item.getOrcamentoItemCampoValor()) {
                campo.setOrcamentoItem(item);
                orcamentoItemCampoValorService.validarObjeto(campo, item.getOrcamentoItemCampoValor());

                if (campo.getIdOrcamentoItemCampoValor() != null
                        && campo.getIdOrcamentoItemCampoValor() == 0) {
                    campo.setIdOrcamentoItemCampoValor(null);
                }
            }
        }

        // obs: ira ser igual pois ainda nao existe regra comercial. Ex: desconto,
        // acrescimo, etc
        objeto.setVlPrecoBase(totalOrcamento);
        objeto.setVlPrecoFinal(totalOrcamento);

        return repository.save(objeto);
    }

    public BigDecimal aplicarMetodoPrecificacao(OrcamentoItem item, Long idEmpresaMetodoPrecificacao) {

        EmpresaMetodoPrecificacao empresaMetodo = empresaMetodoPrecificacaoService
                .buscarPorId(idEmpresaMetodoPrecificacao);

        BigDecimal precoItem = precificacaoService.precificarItem(item, empresaMetodo);

        // orcamentoItemService.validarTotal(item, precoItem);
        // item.setVlPrecoTotal(precoItem);

        return precoItem;

    }

    public BigDecimal calcularPrecoItem(
            OrcamentoItem item,
            Long idEmpresaMetodoPrecificacao) {

        EmpresaMetodoPrecificacao empresaMetodo = empresaMetodoPrecificacaoService
                .buscarPorId(idEmpresaMetodoPrecificacao);

        return precificacaoService.precificarItem(item, empresaMetodo);
    }

    public BigDecimal previewPrecificacao(Orcamento objeto) {

        BigDecimal total = BigDecimal.ZERO;

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }

        if (objeto.getIdEmpresaMetodoPrecificacao() == null) {

            EmpresaMetodoPrecificacao empresaMetodoPrecificacao = empresaMetodoPrecificacaoService
                    .obterEmpresaMetodoPrecificacaoSimples(objeto.getIdTenant());
            objeto.setIdEmpresaMetodoPrecificacao(empresaMetodoPrecificacao.getIdEmpresaMetodoPrecificacao());
        }

        for (OrcamentoItem item : objeto.getOrcamentoItem()) {

            if (item.getIdCatalogo() == null) {
                return total;
            }

            BigDecimal valorItem = calcularPrecoItem(
                    item,
                    objeto.getIdEmpresaMetodoPrecificacao());

            total = total.add(valorItem);
        }

        return total;
    }

    public void validarObjeto(Orcamento objeto) throws Exception {

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }

        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(SEQUENCIA_FUNCTION.apply(objeto), objeto.getIdTenant()),
                ID_FUNCTION);

        if (objeto.getOrcamentoItem() == null || objeto.getOrcamentoItem().isEmpty()) {
            throw new IllegalArgumentException("O orçamento deve possuir ao menos um item.");
        }

        if (objeto.getDtValido().isBefore(objeto.getDtEmissao())) {
            throw new IllegalArgumentException("A data de validade não pode ser anterior à emissão.");
        }

        BigDecimal custoCalculado = BigDecimal.ZERO;
        BigDecimal precoCalculado = BigDecimal.ZERO;

        for (OrcamentoItem item : objeto.getOrcamentoItem()) {

            BigDecimal custoItem = item.getVlCustoUnitario()
                    .multiply(item.getQtItem());

            custoCalculado = custoCalculado.add(custoItem);

            precoCalculado = precoCalculado.add(item.getVlPrecoTotal());
        }

        custoCalculado = custoCalculado.setScale(2, RoundingMode.HALF_UP);
        precoCalculado = precoCalculado.setScale(2, RoundingMode.HALF_UP);

        BigDecimal precoBaseInformado = objeto.getVlPrecoBase().setScale(2, RoundingMode.HALF_UP);

    }

    public void alterarStatus(
            Long idOrcamento,
            StatusOrcamento novoStatus) throws Exception {

        Orcamento orcamento = repository.findById(idOrcamento)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado."));

        StatusOrcamento statusAtual = orcamento.getTpStatus();

        validarTransicao(statusAtual, novoStatus);

        orcamento.setTpStatus(novoStatus);
        repository.save(orcamento);

        statusHistoricoService.registrar(
                orcamento,
                statusAtual,
                novoStatus);
    }

    private void validarTransicao(
            StatusOrcamento atual,
            StatusOrcamento novo) {

        if (atual == StatusOrcamento.RASCUNHO && novo == StatusOrcamento.GERADO)
            return;
        if (atual == StatusOrcamento.GERADO && novo == StatusOrcamento.ENVIADO)
            return;
        if (atual == StatusOrcamento.ENVIADO &&
                (novo == StatusOrcamento.APROVADO || novo == StatusOrcamento.REJEITADO))
            return;

        throw new IllegalStateException(
                "Transição de status inválida: " + atual + " → " + novo);
    }

    public String sequencia(String idTenant) throws Exception {

        ConfiguracaoOrcamento config = configuracaoOrcamentoService.obterPrimeiroObjeto(idTenant);

        String sq_sequencia = validacaoService.gerarSequencia(repository.obterSequencial(idTenant));

        String sequenciaFinal = config.getPrefixoNumero() + "-" + sq_sequencia;

        return sequenciaFinal;
    }

    @Transactional(rollbackFor = Exception.class)
    public void excluir(Long id) throws Exception {

        // orcamentoItemService.excluirPorMestre(id);

        repository.deleteById(id);
    }
}
