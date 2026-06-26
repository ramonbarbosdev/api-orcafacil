package com.api_orcafacil.dto.precificacao;

import java.util.Map;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.model.MetodoPrecificacao;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaMetodoPrecificacaoResponse {

    private Long idEmpresaMetodoPrecificacao;
    private Long idMetodoPrecificacao;
    private Long idOrganizacao;
    private TipoPrecificacao cdMetodoPrecificacao;
    private String nmMetodoPrecificacao;
    private Map<String, Object> configuracao;

    public static EmpresaMetodoPrecificacaoResponse from(
            EmpresaMetodoPrecificacao empresa,
            MetodoPrecificacao metodo) {
        EmpresaMetodoPrecificacaoResponse response = new EmpresaMetodoPrecificacaoResponse();
        response.setIdEmpresaMetodoPrecificacao(empresa.getIdEmpresaMetodoPrecificacao());
        response.setIdMetodoPrecificacao(empresa.getIdMetodoPrecificacao());
        response.setIdOrganizacao(empresa.getIdOrganizacao());
        response.setConfiguracao(empresa.getConfiguracao());
        if (metodo != null) {
            response.setCdMetodoPrecificacao(metodo.getCdMetodoPrecificacao());
            response.setNmMetodoPrecificacao(metodo.getNmMetodoPrecificacao());
        }
        return response;
    }
}
