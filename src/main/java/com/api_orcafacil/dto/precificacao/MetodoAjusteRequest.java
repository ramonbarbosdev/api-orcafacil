package com.api_orcafacil.dto.precificacao;

import com.api_orcafacil.common.TipoAjuste;
import com.api_orcafacil.common.TipoOperacaoAjuste;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetodoAjusteRequest {

    private Long idMetodoAjuste;
    private Long idEmpresaMetodoPrecificacao;
    private Long idCampoPersonalizado;
    private TipoAjuste tpAjuste;
    private TipoOperacaoAjuste tpOperacao;
    private String vlCondicao;
    private Double vlIncremento;
}
