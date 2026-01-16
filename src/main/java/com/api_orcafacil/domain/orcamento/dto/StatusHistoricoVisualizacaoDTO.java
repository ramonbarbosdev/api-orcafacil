package com.api_orcafacil.domain.orcamento.dto;

import java.time.LocalDateTime;

import com.api_orcafacil.enums.StatusOrcamento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistoricoVisualizacaoDTO {
    private StatusOrcamento statusAnterior;

    private StatusOrcamento statusAtual;

    private LocalDateTime dataHora;

    private String usuario;
}
