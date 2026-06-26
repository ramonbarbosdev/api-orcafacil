package com.api_orcafacil.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.api_orcafacil.common.TipoItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orcamento_item")
public class OrcamentoItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento_item")
    @SequenceGenerator(name = "seq_orcamento_item", sequenceName = "seq_orcamento_item", allocationSize = 1)
    @Column(name = "id_orcamentoitem")
    private Long idOrcamentoItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orcamento", nullable = false)
    @JsonIgnore
    private Orcamento orcamento;

    @Column(name = "id_catalogo", nullable = false)
    private Long idCatalogo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo", insertable = false, updatable = false)
    @JsonIgnore
    private Catalogo catalogo;

    @Column(name = "qt_item", nullable = false, precision = 18, scale = 4)
    private BigDecimal qtItem;

    @Column(name = "vl_custounitario", nullable = false, precision = 18, scale = 4)
    private BigDecimal vlCustoUnitario;

    @Column(name = "vl_precounitario", nullable = false, precision = 18, scale = 4)
    private BigDecimal vlPrecoUnitario;

    @Column(name = "vl_precototal", nullable = false, precision = 18, scale = 4)
    private BigDecimal vlPrecoTotal;

    @OneToMany(mappedBy = "orcamentoItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrcamentoItemCampoValor> camposValor = new ArrayList<>();

    public String getCdCatalogo() {
        return catalogo != null ? catalogo.getCdCatalogo() : null;
    }

    public String getNmCatalogo() {
        return catalogo != null ? catalogo.getNmCatalogo() : null;
    }

    @Transient
    public TipoItem getTpItem() {
        return catalogo != null ? catalogo.getTpItem() : null;
    }
}
