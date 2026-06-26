package com.api_orcafacil.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.springframework.stereotype.Service;

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
