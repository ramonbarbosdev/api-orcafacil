package com.api_orcafacil.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "catalogo_campo")
public class CatalogoCampo extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_catalogo_campo")
    @SequenceGenerator(name = "seq_catalogo_campo", sequenceName = "seq_catalogo_campo", allocationSize = 1)
    @Column(name = "id_catalogo_campo")
    private Long idCatalogoCampo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo", nullable = false)
    @JsonIgnore
    private Catalogo catalogo;

    @Column(name = "id_campopersonalizado", nullable = false)
    private Long idCampoPersonalizado;

    @Column(name = "vl_padrao")
    private String vlPadrao;

    @Column(name = "ds_descricao")
    private String dsDescricao;

    @Column(name = "fl_editavel", nullable = false)
    private Boolean flEditavel = true;

    @Column(name = "ordem")
    private Integer ordem;
}
