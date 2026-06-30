package com.api_orcafacil.dto.dashboard;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardSerieMensalDTO {

    private String mes;
    private long totalOrcamentos;
    private BigDecimal faturamentoAprovado;
}
