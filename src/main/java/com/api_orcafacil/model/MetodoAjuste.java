package com.api_orcafacil.model;

import com.api_orcafacil.common.TipoAjuste;
import com.api_orcafacil.common.TipoOperacaoAjuste;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "metodo_ajustes")
public class MetodoAjuste extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_metodo_ajuste")
    @SequenceGenerator(name = "seq_metodo_ajuste", sequenceName = "seq_metodo_ajuste", allocationSize = 1)
    @Column(name = "id_metodoajuste")
    private Long idMetodoAjuste;

    @Column(name = "id_empresametodoprecificacao", nullable = false)
    private Long idEmpresaMetodoPrecificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresametodoprecificacao", insertable = false, updatable = false)
    @JsonIgnore
    private EmpresaMetodoPrecificacao empresaMetodoPrecificacao;

    @Column(name = "id_campopersonalizado", nullable = false)
    private Long idCampoPersonalizado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_campopersonalizado", insertable = false, updatable = false)
    @JsonIgnore
    private CampoPersonalizado campoPersonalizado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_ajuste", nullable = false, length = 30)
    private TipoAjuste tpAjuste;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_operacao", nullable = false, length = 30)
    private TipoOperacaoAjuste tpOperacao;

    @Column(name = "vl_condicao")
    private String vlCondicao;

    @Column(name = "vl_incremento", nullable = false)
    private Double vlIncremento;

    @JsonProperty("nmCampoPersonalizado")
    public String getNmCampoPersonalizado() {
        return campoPersonalizado != null ? campoPersonalizado.getNmCampoPersonalizado() : null;
    }
}
