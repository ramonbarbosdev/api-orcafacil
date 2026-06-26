package com.api_orcafacil.dto.precificacao;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaMetodoPrecificacaoRequest {

    private Long idEmpresaMetodoPrecificacao;
    private Long idMetodoPrecificacao;
    private Map<String, Object> configuracao;
}
