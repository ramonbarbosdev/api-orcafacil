package com.api_orcafacil.domain.precificacao.model;

import java.time.LocalDateTime;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "metodo_ajustes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetodoAjuste {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_metodo_ajuste")
    @SequenceGenerator(
        name = "seq_metodo_ajuste",
        sequenceName = "seq_metodo_ajuste",
        allocationSize = 1
    )
    @Column(name = "id_metodoajuste")
    private Long idMetodoAjuste;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empresa", insertable = false, updatable = false)
    @JsonIgnore
    private Empresa empresa;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_campopersonalizado", insertable = false, updatable = false)
    private CampoPersonalizado campoPersonalizado;

    @Column(name = "id_campopersonalizado")
    private Long idCampoPersonalizado;

    @Column(name = "vl_incremento", nullable = false)
    private Double vlIncremento;
    // Ex: 0.2, 0.3

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }
}