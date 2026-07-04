package com.api_orcafacil.dto.condicao;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CondicaoPagamentoRequest {

    private Long idCondicaoPagamento;

    @NotBlank(message = "Codigo da condicao e obrigatorio")
    private String cdCondicaoPagamento;

    @NotBlank(message = "Nome da condicao e obrigatorio")
    private String nmCondicaoPagamento;
}
