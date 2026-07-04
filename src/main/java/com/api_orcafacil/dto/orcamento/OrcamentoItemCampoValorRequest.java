package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;

import com.api_orcafacil.common.TipoCampoValor;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoItemCampoValorRequest {

    private Long idOrcamentoItemCampoValor;

    @NotNull(message = "Campo personalizado e obrigatorio")
    private Long idCampoPersonalizado;

    private TipoCampoValor tpValor;

    @NotNull(message = "Valor informado e obrigatorio")
    private BigDecimal vlInformado;

    private String dsDescricao;
}
