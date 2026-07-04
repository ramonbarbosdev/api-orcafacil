package com.api_orcafacil.dto.precificacao;

import com.api_orcafacil.common.TipoPrecificacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetodoPrecificacaoRequest {

    @NotNull(message = "Tipo do metodo e obrigatorio")
    private TipoPrecificacao cdMetodoPrecificacao;

    @NotBlank(message = "Nome do metodo e obrigatorio")
    private String nmMetodoPrecificacao;

    private String dsMetodoPrecificacao;
}
