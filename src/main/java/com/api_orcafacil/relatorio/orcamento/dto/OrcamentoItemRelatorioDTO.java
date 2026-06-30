package com.api_orcafacil.relatorio.orcamento.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoItemRelatorioDTO {

    private String codigo;
    private String descricao;
    private BigDecimal quantidade;
    private BigDecimal precoCusto;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;
    private String materiaisTexto;
}
