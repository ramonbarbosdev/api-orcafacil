package com.api_orcafacil.dto.servico;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaServicoRequest {

    private Long idCategoriaServico;

    @NotBlank(message = "Codigo da categoria e obrigatorio")
    private String cdCategoriaServico;

    @NotBlank(message = "Nome da categoria e obrigatorio")
    private String nmCategoriaServico;

    private String dsCategoriaServico;
}
