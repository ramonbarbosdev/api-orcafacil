package com.api_orcafacil.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.api_orcafacil.common.TipoItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "catalogo")
public class Catalogo extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_catalogo")
    @SequenceGenerator(name = "seq_catalogo", sequenceName = "seq_catalogo", allocationSize = 1)
    @Column(name = "id_catalogo")
    private Long idCatalogo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_item")
    private TipoItem tpItem;

    @Column(name = "cd_catalogo", nullable = false, length = 50)
    private String cdCatalogo;

    @Column(name = "nm_catalogo", nullable = false)
    private String nmCatalogo;

    @Column(name = "ds_catalogo", columnDefinition = "text")
    private String dsCatalogo;

    @Column(name = "vl_custobase", precision = 18, scale = 4)
    private BigDecimal vlCustoBase;

    @Column(name = "vl_precobase", precision = 18, scale = 4)
    private BigDecimal vlPrecoBase;

    @OneToMany(mappedBy = "catalogo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CatalogoCampo> campos = new ArrayList<>();
}
