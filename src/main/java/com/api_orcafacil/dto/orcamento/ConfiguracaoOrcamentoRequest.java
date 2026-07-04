package com.api_orcafacil.dto.orcamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfiguracaoOrcamentoRequest {

    @NotBlank(message = "Prefixo do numero e obrigatorio")
    private String prefixoNumero;

    @NotNull(message = "Validade em dias e obrigatoria")
    @Positive(message = "Validade em dias deve ser positiva")
    private Integer validadeDias;

    private String termosPadrao;
}
