package com.api_orcafacil.domain.catalogo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.api_orcafacil.domain.precificacao.model.CampoPersonalizado;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "catalogo_campo")
public class CatalogoCampo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_catalogo_campo")
    @SequenceGenerator(name = "seq_catalogo_campo", sequenceName = "seq_catalogo_campo", allocationSize = 1)
    @Column(name = "id_catalogo_campo")
    private Long idCatalogoCampo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo", insertable = false, updatable = false)
    @JsonIgnore
    private Catalogo catalogo;

    @Column(name = "id_catalogo", nullable = false)
    @NotNull(message = "O catalogo é obrigatorio!")
    private Long idCatalogo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_campopersonalizado", insertable = false, updatable = false)
    @JsonIgnore
    private CampoPersonalizado campoPersonalizado;

    @Column(name = "id_campopersonalizado", nullable = false)
    @NotNull(message = "O campo é obrigatorio!")
    private Long idCampoPersonalizado;

    @Column(name = "vl_padrao")
    private String vlPadrao;

    @Column(name = "fl_editavel", nullable = false)
    private Boolean flEditavel = true;

    @Column(name = "ordem")
    private Integer ordem;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }

    @JsonProperty("nmCampoPersonalizado")
    public String getNmCampoPersonalizado() {

        if (campoPersonalizado != null) {
            return campoPersonalizado.getNmCampoPersonalizado();
        }
        return null;
    }

}
