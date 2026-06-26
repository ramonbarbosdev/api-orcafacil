package com.api_orcafacil.dto.precificacao;

import com.api_orcafacil.common.TipoCampoValor;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampoPersonalizadoRequest {

    private Long idCampoPersonalizado;

    @NotBlank
    private String cdCampoPersonalizado;

    @NotBlank
    private String nmCampoPersonalizado;

    private String dsCampoPersonalizado;

    @NotBlank
    private String tpCampoPersonalizado;

    private TipoCampoValor tpCampoValor;
}
