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

        BigDecimal quantidade = item.getQtItem();
        BigDecimal custoUnitario = item.getVlCustoUnitario();

        // 1️⃣ custo base
        BigDecimal custoBase = custoUnitario.multiply(quantidade);

        // 2️⃣ materiais / campos personalizados
        BigDecimal materiais = BigDecimal.ZERO;

        if (item.getOrcamentoItemCampoValor() != null) {
            for (OrcamentoItemCampoValor campo : item.getOrcamentoItemCampoValor()) {
                if (campo.getVlInformado() != null && !campo.getVlInformado().isBlank()) {
                    materiais = materiais.add(
                            new BigDecimal(campo.getVlInformado()));
                }
            }
        }

        BigDecimal baseCalculo = custoBase.add(materiais);

        // 3️⃣ aplica método conforme ENUM
        BigDecimal precoFinal = aplicarMetodo(
                baseCalculo,
                metodo.getCdMetodoPrecificacao(),
                empresaMetodo.getConfiguracao());

        return precoFinal.setScale(2, RoundingMode.HALF_UP);
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
