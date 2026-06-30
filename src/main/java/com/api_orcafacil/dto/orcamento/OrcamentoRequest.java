package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.model.Cliente;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoRequest {

    private Long idOrcamento;
    @NotBlank(message = "Numero do orcamento e obrigatorio")
    private String nuOrcamento;
    @NotNull(message = "Data de emissao e obrigatoria")
    private LocalDate dtEmissao;
    @NotNull(message = "Data de validade e obrigatoria")
    private LocalDate dtValido;
    private Long idCliente;
    @NotNull(message = "Cliente e obrigatorio")
    private Cliente cliente;
    private Long idEmpresaMetodoPrecificacao;
    @NotNull(message = "Condicao de pagamento e obrigatoria")
    private Long idCondicaoPagamento;
    private Integer nuPrazoEntrega;
    private String dsObservacoes;
    private BigDecimal vlPrecoBase;
    private BigDecimal vlPrecoFinal;
    private StatusOrcamento tpStatus;
    @NotEmpty(message = "O orcamento deve possuir ao menos um item")
    @Valid
    private List<OrcamentoItemRequest> itens;
}
