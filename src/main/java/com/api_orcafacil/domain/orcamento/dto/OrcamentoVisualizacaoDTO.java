package com.api_orcafacil.domain.orcamento.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.api_orcafacil.enums.StatusOrcamento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoVisualizacaoDTO {
 
    private Long idOrcamento;
    private String nuOrcamento;
    private LocalDate dtEmissao;
    private LocalDate dtValido;
    private StatusOrcamento status;

    private ClienteVisualizacaoDTO cliente;

    private String metodoPrecificacao;
    private BigDecimal vlPrecoBase;
    private BigDecimal vlPrecoFinal;

    private List<ItemVisualizacaoDTO> itens;
    private List<StatusHistoricoVisualizacaoDTO> historicoStatus;
}
