package com.api_orcafacil.dto.servico;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicoRequest {

    private Long idServico;
    private Long idCategoriaServico;

    @NotBlank
    private String cdServico;

    @NotBlank
    private String nmServico;

    private String dsServico;
    private BigDecimal vlCusto;
    private BigDecimal vlPreco;
}
