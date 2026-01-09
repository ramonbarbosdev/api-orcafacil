package com.api_orcafacil.domain.orcamento.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.repository.EmpresaRepository;
import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItem;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItemCampoValor;
import com.api_orcafacil.domain.orcamento.repository.CondicaoPagamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.ConfiguracaoOrcamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoItemCampoValorRepository;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoItemRepository;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.servico.repository.ServicoRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.util.MestreDetalheUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class OrcamentoItemService {

    @Autowired
    private OrcamentoItemRepository repository;

    @Autowired
    private OrcamentoItemCampoValorService orcamentoItemCampoValorService;

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<OrcamentoItem, Long> ID_FUNCTION = OrcamentoItem::getIdOrcamentoItem;

    public void validarObjeto(OrcamentoItem objeto) throws Exception {

        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(objeto.getIdCatalogo()),
                ID_FUNCTION,
                "Não foi possivel salvar, existe itens repetidos. Verifique a listagem ou utilize um novo item.");

        validarValoresItem(objeto);
    }

    public void validarValoresItem(OrcamentoItem item) {

        if (item.getQtItem() == null)
            throw new IllegalArgumentException("Quantidade do item não informada.");

        if (item.getVlCustoUnitario() == null)
            throw new IllegalArgumentException("Custo unitário não informado.");

        BigDecimal quantidade = item.getQtItem();
        BigDecimal custoUnitario = item.getVlCustoUnitario();

        BigDecimal somaMateriais = BigDecimal.ZERO;

        if (item.getOrcamentoItemCampoValor() != null) {
            for (OrcamentoItemCampoValor campo : item.getOrcamentoItemCampoValor()) {
                if (campo.getVlInformado() != null && !campo.getVlInformado().isBlank()) {
                    try {
                        somaMateriais = somaMateriais.add(
                                new BigDecimal(campo.getVlInformado()));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "Valor inválido no material: " + campo.getVlInformado());
                    }
                }
            }
        }

        //  TOTAL = (custo unitário + materiais) × quantidade
        BigDecimal totalCalculado = custoUnitario
                .add(somaMateriais)
                .multiply(quantidade)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalInformado = item.getVlPrecoTotal();

        if (totalInformado == null)
            throw new IllegalArgumentException("Total do item não informado.");

        totalInformado = totalInformado.setScale(2, RoundingMode.HALF_UP);

        if (totalCalculado.compareTo(totalInformado) != 0) {
            throw new IllegalArgumentException(
                    String.format(
                            "Total do item inválido. Esperado: %s, Informado: %s",
                            totalCalculado,
                            totalInformado));
        }
    }

    @PersistenceContext
    private EntityManager entityManager;

    public void excluirPorMestre(Long idMestre) throws Exception {

        List<OrcamentoItem> objetos = repository.findbyIdMestre(idMestre);

        for (OrcamentoItem orcamentoItem : objetos) {
            orcamentoItemCampoValorService.excluirPorMestre(orcamentoItem.getIdOrcamentoItem());

        }

        repository.deleteByIdMestre(idMestre);
    }

    @Transactional(rollbackFor = Exception.class)
    public void excluir(Long id) throws Exception {

        repository.deleteById(id);
    }

}
