package com.api_orcafacil.dto.precificacao;

import com.api_orcafacil.common.TipoAjuste;
import com.api_orcafacil.common.TipoOperacaoAjuste;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetodoAjusteRequest {

    private Long idMetodoAjuste;
    private Long idEmpresaMetodoPrecificacao;

    @NotNull(message = "Campo personalizado e obrigatorio")
    private Long idCampoPersonalizado;

    @NotNull(message = "Tipo de ajuste e obrigatorio")
    private TipoAjuste tpAjuste;

    @NotNull(message = "Tipo de operacao e obrigatorio")
    private TipoOperacaoAjuste tpOperacao;

    private String vlCondicao;

    @NotNull(message = "Incremento e obrigatorio")
    private Double vlIncremento;
}
