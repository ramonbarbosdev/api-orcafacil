package com.api_orcafacil.dto.precificacao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanoAssinaturaRequest {

    private String nmPlanoAssinatura;
    private Double vlMensal;
    private Integer nuLimiteMensagens;
    private Integer nuLimiteAtendentes;
    private Boolean flAtivo;
}
