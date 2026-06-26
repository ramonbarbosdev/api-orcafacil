package com.api_orcafacil.dto.precificacao;

import java.util.List;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.model.MetodoPrecificacao;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetodoPrecificacaoResponse {

    private Long idMetodoPrecificacao;
    private TipoPrecificacao cdMetodoPrecificacao;
    private String nmMetodoPrecificacao;
    private String dsMetodoPrecificacao;
    private List<CampoMetodoDTO> campos;

    public static MetodoPrecificacaoResponse from(MetodoPrecificacao m, List<CampoMetodoDTO> campos) {
        MetodoPrecificacaoResponse r = new MetodoPrecificacaoResponse();
        r.setIdMetodoPrecificacao(m.getIdMetodoPrecificacao());
        r.setCdMetodoPrecificacao(m.getCdMetodoPrecificacao());
        r.setNmMetodoPrecificacao(m.getNmMetodoPrecificacao());
        r.setDsMetodoPrecificacao(m.getDsMetodoPrecificacao());
        r.setCampos(campos);
        return r;
    }
}
