package com.api_orcafacil.domain.empresa.model;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

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


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empresa_metodo_precificacao")
public class EmpresaMetodoPrecificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_empresametodoprecificacao")
    @SequenceGenerator(name = "seq_empresametodoprecificacao", sequenceName = "seq_empresametodoprecificacao", allocationSize = 1)
    @Column(name = "id_empresametodoprecificacao")
    private Long idEmpresaMetodoPrecificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodoprecificacao", insertable = false, updatable = false)
    @JsonIgnore
    private MetodoPrecificacao metodoprecificacao;

    @Column(name = "id_metodoprecificacao")
    private Long idMetodoPrecificacao;

    @Column(name = "id_tenant", nullable = false)
    private String idTenant;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuracao", columnDefinition = "jsonb")
    private Map<String, Object> configuracao;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }

 
}
