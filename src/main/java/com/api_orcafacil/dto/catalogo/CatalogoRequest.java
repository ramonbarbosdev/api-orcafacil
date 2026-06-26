package com.api_orcafacil.dto.catalogo;

import java.math.BigDecimal;
import java.util.List;

import com.api_orcafacil.common.TipoItem;
import com.api_orcafacil.model.CatalogoCampo;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogoRequest {

    private Long idCatalogo;
    private TipoItem tpItem;

    @NotBlank
    private String cdCatalogo;

    @NotBlank
    private String nmCatalogo;

    private String dsCatalogo;
    private BigDecimal vlCustoBase;
    private BigDecimal vlPrecoBase;
    private List<CatalogoCampo> campos;
}
