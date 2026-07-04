package com.api_orcafacil.dto.precificacao;

import java.util.Map;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaMetodoPrecificacaoRequest {

    private Long idEmpresaMetodoPrecificacao;

    @NotNull(message = "Metodo de precificacao e obrigatorio")
    private Long idMetodoPrecificacao;

    private Map<String, Object> configuracao;
}
