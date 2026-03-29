package com.api_orcafacil.domain.orcamento.dto;

import java.math.BigDecimal;
import java.util.List;

import com.api_orcafacil.enums.TipoItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVisualizacaoDTO {

    private Long idItem;

    private String descricao;

    private BigDecimal quantidade;

    private BigDecimal precoCusto;
    private BigDecimal precoUnitario;

    private BigDecimal subtotal;

    private TipoItem tipo;

    private List<MaterialVisualizacaoDTO> materiais;

    public List<MaterialVisualizacaoDTO> getMateriais() {
        return materiais != null ? materiais : List.of();
    }

    

}
