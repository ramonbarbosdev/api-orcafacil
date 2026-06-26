package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;

import com.api_orcafacil.common.TipoCampoValor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialVisualizacaoDTO {

    private String nome;
    private String descricao;
    private BigDecimal valor;
    private TipoCampoValor tipo;
}
