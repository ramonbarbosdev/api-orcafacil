package com.api_orcafacil.dto.precificacao;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanoAssinaturaResponse {

    private Long idPlanoAssinatura;
    private String nmPlanoAssinatura;
    private Double vlMensal;
    private Integer nuLimiteMensagens;
    private Integer nuLimiteAtendentes;
    private Boolean flAtivo;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;
}
