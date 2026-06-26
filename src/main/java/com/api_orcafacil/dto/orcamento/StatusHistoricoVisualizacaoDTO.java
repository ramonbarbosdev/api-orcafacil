package com.api_orcafacil.dto.orcamento;

import java.time.LocalDateTime;

import com.api_orcafacil.common.StatusOrcamento;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusHistoricoVisualizacaoDTO {

    private StatusOrcamento statusAnterior;
    private StatusOrcamento statusAtual;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataHora;

    private String usuario;
}
