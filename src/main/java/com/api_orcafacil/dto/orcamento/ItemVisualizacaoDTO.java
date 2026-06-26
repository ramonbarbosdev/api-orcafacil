package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;
import java.util.List;

import com.api_orcafacil.common.TipoItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemVisualizacaoDTO {

    private Long idItem;
    private String codigo;
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
