package com.api_orcafacil.domain.empresa.model;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", insertable = false, updatable = false)
    @JsonIgnore
    private Empresa empresa;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "configuracao", columnDefinition = "jsonb")
    private String configuracao;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }

}
