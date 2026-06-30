package com.api_orcafacil.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.api_orcafacil.common.StatusOrcamento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardOrcamentoRecenteDTO {

    private Long idOrcamento;
    private String nuOrcamento;
    private String nmCliente;
    private BigDecimal vlPrecoFinal;
    private LocalDate dtEmissao;
    private LocalDate dtValido;
    private StatusOrcamento tpStatus;
}
