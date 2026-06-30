package com.api_orcafacil.relatorio.orcamento.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoRelatorioDTO {

    private String nuOrcamento;
    private String dtEmissaoFormatada;
    private String dtValidoFormatada;
    private String clienteNome;
    private String clienteCpfCnpj;
    private String nmEmpresa;
    private BigDecimal vlPrecoBase;
    private BigDecimal totalDesconto;
    private BigDecimal vlPrecoFinal;
    private String observacoes;
    private String condicaoPagamentoTexto;
    private String prazoEntregaTexto;
    private Boolean possuiProdutos;
    private Boolean possuiServicos;
    private List<OrcamentoItemRelatorioDTO> produtos = new ArrayList<>();
    private List<OrcamentoItemRelatorioDTO> servicos = new ArrayList<>();
}
