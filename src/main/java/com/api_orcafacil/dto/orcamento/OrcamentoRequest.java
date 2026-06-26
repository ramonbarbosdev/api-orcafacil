package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.model.OrcamentoItem;
import com.api_orcafacil.model.Cliente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoRequest {

    private Long idOrcamento;
    private String nuOrcamento;
    private LocalDate dtEmissao;
    private LocalDate dtValido;
    private Long idCliente;
    private Cliente cliente;
    private Long idEmpresaMetodoPrecificacao;
    private Long idCondicaoPagamento;
    private Integer nuPrazoEntrega;
    private String dsObservacoes;
    private BigDecimal vlPrecoBase;
    private BigDecimal vlPrecoFinal;
    private StatusOrcamento tpStatus;
    private List<OrcamentoItem> itens;
}
