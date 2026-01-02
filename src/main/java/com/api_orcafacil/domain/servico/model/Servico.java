package com.api_orcafacil.domain.servico.model;

import java.math.BigDecimal;
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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_servico")
    @SequenceGenerator(name = "seq_servico", sequenceName = "seq_servico", allocationSize = 1)
    private Long id_servico;

    @NotBlank(message = "O código é obrigatorio!")
    private String cd_servico;

    @NotBlank(message = "O tenant é obrigatorio!")
    @Column(name = "id_tenant", nullable = false)
    private String idTenant;

    @NotBlank(message = "O nome é obrigatorio!")
    private String nm_servico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoriaservico", insertable = false, updatable = false)
    @JsonIgnore
    private CategoriaServico categoriaservico;

    @NotNull(message = "A categoria é obrigatorio!")
    private Long id_categoriaservico;

    private BigDecimal vl_preco;

    private String ds_observacoes;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dt_cadastro;

    @PrePersist
    protected void onCreate() {
        this.dt_cadastro = LocalDateTime.now();
    }

    public Long getId_servico() {
        return id_servico;
    }

    public void setId_servico(Long id_servico) {
        this.id_servico = id_servico;
    }

    public String getCd_servico() {
        return cd_servico;
    }

    public void setCd_servico(String cd_servico) {
        this.cd_servico = cd_servico;
    }

    public String getIdTenant() {
        return idTenant;
    }

    public void setIdTenant(String idTenant) {
        this.idTenant = idTenant;
    }

    public String getNm_servico() {
        return nm_servico;
    }

    public void setNm_servico(String nm_servico) {
        this.nm_servico = nm_servico;
    }

    public CategoriaServico getCategoriaservico() {
        return categoriaservico;
    }

    public void setCategoriaservico(CategoriaServico categoriaservico) {
        this.categoriaservico = categoriaservico;
    }

    public Long getId_categoriaservico() {
        return id_categoriaservico;
    }

    public void setId_categoriaservico(Long id_categoriaservico) {
        this.id_categoriaservico = id_categoriaservico;
    }

    public BigDecimal getVl_preco() {
        return vl_preco;
    }

    public void setVl_preco(BigDecimal vl_preco) {
        this.vl_preco = vl_preco;
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

    @Transient
    public String getNm_categoriaservico() {

        if (categoriaservico != null) {
            return categoriaservico.getNm_categoriaservico();
        }
        return null;
    }

}