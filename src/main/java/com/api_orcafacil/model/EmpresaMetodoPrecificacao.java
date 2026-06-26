package com.api_orcafacil.model;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.api_orcafacil.common.TipoPrecificacao;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "empresa_metodo_precificacao")
public class EmpresaMetodoPrecificacao extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_empresametodoprecificacao")
    @SequenceGenerator(name = "seq_empresametodoprecificacao", sequenceName = "seq_empresametodoprecificacao", allocationSize = 1)
    @Column(name = "id_empresametodoprecificacao")
    private Long idEmpresaMetodoPrecificacao;

    @Column(name = "id_metodoprecificacao", nullable = false)
    private Long idMetodoPrecificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodoprecificacao", insertable = false, updatable = false)
    @JsonIgnore
    private MetodoPrecificacao metodoPrecificacao;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuracao", columnDefinition = "jsonb")
    private Map<String, Object> configuracao;

    @JsonProperty("nmMetodoPrecificacao")
    public String getNmMetodoPrecificacao() {
        return metodoPrecificacao != null ? metodoPrecificacao.getNmMetodoPrecificacao() : null;
    }

    @JsonProperty("cdMetodoPrecificacao")
    public TipoPrecificacao getCdMetodoPrecificacao() {
        return metodoPrecificacao != null ? metodoPrecificacao.getCdMetodoPrecificacao() : null;
    }
}
