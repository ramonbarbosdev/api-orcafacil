package com.api_orcafacil.domain.orcamento.dto;

import java.math.BigDecimal;

import com.api_orcafacil.enums.TipoCampoValor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialVisualizacaoDTO {
    
    
    private String descricao;
    private String unidade;
    private BigDecimal quantidade;
    private String valor;
    private TipoCampoValor tipo;
}
