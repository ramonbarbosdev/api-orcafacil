package com.api_orcafacil.dto.precificacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanoAssinaturaRequest {

    @NotBlank(message = "Nome do plano e obrigatorio")
    private String nmPlanoAssinatura;

    @NotNull(message = "Valor mensal e obrigatorio")
    private Double vlMensal;

    private Integer nuLimiteMensagens;
    private Integer nuLimiteAtendentes;
    private Boolean flAtivo;
}
