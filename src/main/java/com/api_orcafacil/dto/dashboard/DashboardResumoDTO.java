package com.api_orcafacil.dto.dashboard;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardResumoDTO {

    private long totalOrcamentos;
    private long emAndamento;
    private long aprovados;
    private long rejeitados;
    private long rascunhos;
    private Map<String, Long> totaisPorStatus = new LinkedHashMap<>();
    private BigDecimal faturamentoAprovadoMes = BigDecimal.ZERO;
    private BigDecimal variacaoFaturamentoMes;
    private long orcamentosMes;
    private Long variacaoOrcamentosMes;
    private DashboardContagensDTO contagens = new DashboardContagensDTO();
    private List<DashboardOrcamentoRecenteDTO> orcamentosRecentes;
    private List<DashboardSerieMensalDTO> serieMensal;
}
