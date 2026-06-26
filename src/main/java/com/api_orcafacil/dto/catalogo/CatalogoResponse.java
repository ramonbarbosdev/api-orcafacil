package com.api_orcafacil.dto.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.api_orcafacil.common.TipoItem;
import com.api_orcafacil.model.Catalogo;
import com.api_orcafacil.model.CatalogoCampo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogoResponse {

    private Long idCatalogo;
    private Long idOrganizacao;
    private TipoItem tpItem;
    private String cdCatalogo;
    private String nmCatalogo;
    private String dsCatalogo;
    private BigDecimal vlCustoBase;
    private BigDecimal vlPrecoBase;

    @JsonProperty("catalogoCampo")
    private List<CatalogoCampo> campos;

    private LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;

    public static CatalogoResponse from(Catalogo c) {
        CatalogoResponse r = new CatalogoResponse();
        r.setIdCatalogo(c.getIdCatalogo());
        r.setIdOrganizacao(c.getIdOrganizacao());
        r.setTpItem(c.getTpItem());
        r.setCdCatalogo(c.getCdCatalogo());
        r.setNmCatalogo(c.getNmCatalogo());
        r.setDsCatalogo(c.getDsCatalogo());
        r.setVlCustoBase(c.getVlCustoBase());
        r.setVlPrecoBase(c.getVlPrecoBase());
        List<CatalogoCampo> campos = c.getCampos();
        r.setCampos(campos == null ? Collections.emptyList() : new ArrayList<>(campos));
        r.setDtCriacao(c.getDtCriacao());
        r.setDtAtualizacao(c.getDtAtualizacao());
        return r;
    }
}
