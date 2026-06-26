package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.common.TipoItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoVisualizacaoDTO {

    private Long idOrcamento;
    private String nuOrcamento;
    private LocalDate dtEmissao;
    private LocalDate dtValido;
    private StatusOrcamento status;
    private String nmEmpresa;
    private ClienteVisualizacaoDTO cliente;
    private String metodoPrecificacao;
    private BigDecimal vlPrecoBase;
    private BigDecimal vlPrecoFinal;
    private List<ItemVisualizacaoDTO> itens;
    private List<StatusHistoricoVisualizacaoDTO> historicoStatus;
    private BigDecimal totalDesconto;
    private String condicaoPagamento;
    private Integer nuPrazoEntrega;
    private String observacoes;

    public List<ItemVisualizacaoDTO> getProdutos() {
        if (itens == null) return List.of();
        return itens.stream().filter(i -> i.getTipo() == TipoItem.Produto).toList();
    }

    public List<ItemVisualizacaoDTO> getServicos() {
        if (itens == null) return List.of();
        return itens.stream().filter(i -> i.getTipo() == TipoItem.Servico).toList();
    }
}
