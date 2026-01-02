package com.api_orcafacil.domain.servico.model;

import java.time.LocalDateTime;

import com.api_orcafacil.domain.usuario.model.Usuario;
import com.api_orcafacil.enums.TipoCliente;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "categoria_servico")
public class CategoriaServico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_categoriaservico")
    @SequenceGenerator(name = "seq_categoriaservico", sequenceName = "seq_categoriaservico", allocationSize = 1)
    private Long id_categoriaservico;

    @NotBlank(message = "O código é obrigatorio!")
    private String cd_categoriaservico;

    @NotBlank(message = "O tenant é obrigatorio!")
    @Column(name = "id_tenant", nullable = false)
    private String idTenant;

    @NotBlank(message = "O nome é obrigatorio!")
    private String nm_categoriaservico;

    private String ds_observacoes;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dt_cadastro;

    @PrePersist
    protected void onCreate() {
        this.dt_cadastro = LocalDateTime.now();
    }

    public Long getId_categoriaservico() {
        return id_categoriaservico;
    }

    public void setId_categoriaservico(Long id_categoriaservico) {
        this.id_categoriaservico = id_categoriaservico;
    }

    public String getIdTenant() {
        return idTenant;
    }

    public void setIdTenant(String idTenant) {
        this.idTenant = idTenant;
    }

    public String getNm_categoriaservico() {
        return nm_categoriaservico;
    }

    public void setNm_categoriaservico(String nm_categoriaservico) {
        this.nm_categoriaservico = nm_categoriaservico;
    }

    public String getDs_observacoes() {
        return ds_observacoes;
    }

    public void setDs_observacoes(String ds_observacoes) {
        this.ds_observacoes = ds_observacoes;
    }

    public LocalDateTime getDt_cadastro() {
        return dt_cadastro;
    }

    public void setDt_cadastro(LocalDateTime dt_cadastro) {
        this.dt_cadastro = dt_cadastro;
    }

    public String getCd_categoriaservico() {
        return cd_categoriaservico;
    }

    public void setCd_categoriaservico(String cd_categoriaservico) {
        this.cd_categoriaservico = cd_categoriaservico;
    }

}