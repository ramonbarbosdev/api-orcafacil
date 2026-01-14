package com.api_orcafacil.domain.precificacao.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItem;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItemCampoValor;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.enums.TipoPrecificacao;

@Service
public class PrecificacaoService {

    public BigDecimal precificarItem(
            OrcamentoItem item,
            EmpresaMetodoPrecificacao empresaMetodo) {

        MetodoPrecificacao metodo = empresaMetodo.getMetodoprecificacao();

        if (metodo == null || metodo.getCdMetodoPrecificacao() == null) {
            throw new IllegalArgumentException("Método de precificação não definido.");
        }

        if (item.getQtItem() == null) {
            throw new IllegalArgumentException("Quantidade do item não informada.");
        }

        if (item.getVlCustoUnitario() == null) {
            throw new IllegalArgumentException("Custo unitário do item não informado.");
        }

        BigDecimal quantidade = item.getQtItem();
        BigDecimal custoUnitario = item.getVlCustoUnitario();

        BigDecimal baseCalculo = custoUnitario.multiply(quantidade);

        baseCalculo = regraTipoCalculo(item, baseCalculo, quantidade);

        BigDecimal precoFinal = aplicarMetodo(
                baseCalculo,
                metodo.getCdMetodoPrecificacao(),
                empresaMetodo.getConfiguracao());

        return precoFinal.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal regraTipoCalculo(OrcamentoItem item, BigDecimal baseCalculo, BigDecimal quantidade) {

        if (item.getOrcamentoItemCampoValor() != null) {

            for (OrcamentoItemCampoValor campo : item.getOrcamentoItemCampoValor()) {

                if (campo.getVlInformado() == null || campo.getVlInformado().isBlank()) {
                    continue;
                }

                BigDecimal valor = new BigDecimal(campo.getVlInformado());

                if (campo.getTpValor() == null) {
                    throw new IllegalArgumentException(
                            "Tipo de valor do campo de precificação não informado.");
                }

                switch (campo.getTpValor()) {

                    case PRECO_FIXO:
                        baseCalculo = baseCalculo.add(valor);
                        break;

                    case CUSTO_UNITARIO:
                        baseCalculo = baseCalculo.add(
                                valor.multiply(quantidade));
                        break;

                    case AJUSTE_METODO:
                        // não entra na base
                        break;
                }
            }
        }

        return baseCalculo;
    }

    private BigDecimal aplicarMetodo(
            BigDecimal base,
            TipoPrecificacao tipo,
            Map<String, Object> config) {

        switch (tipo) {

            case MARKUP:
                BigDecimal percentualMarkup = obterDecimal(config, "percentual");

                return base.multiply(
                        BigDecimal.ONE.add(percentualMarkup));

            case MARGEM:
                BigDecimal percentualMargem = obterDecimal(config, "percentual");

                return base.divide(
                        BigDecimal.ONE.subtract(percentualMargem),
                        4,
                        RoundingMode.HALF_UP);

            case FIXO:
                BigDecimal valorFixo = obterDecimal(config, "valor");

                return base.add(valorFixo);
            case SIMPLES:

                return base;

            default:
                throw new IllegalArgumentException(
                        "Tipo de precificação não suportado: " + tipo);
        }
    }

    private BigDecimal obterDecimal(
            Map<String, Object> config,
            String chave) {

        if (config == null || !config.containsKey(chave)) {
            throw new IllegalArgumentException(
                    "Configuração obrigatória não encontrada: " + chave);
        }

        try {
            return new BigDecimal(config.get(chave).toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Valor inválido para configuração: " + chave);
        }
    }
}
