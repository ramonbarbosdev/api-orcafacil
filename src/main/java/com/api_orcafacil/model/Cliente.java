package com.api_orcafacil.model;

import java.time.LocalDateTime;

import com.api_orcafacil.enums.TipoCliente;
import com.api_orcafacil.model.usuario.Usuario;
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
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cliente")
    @SequenceGenerator(name = "seq_cliente", sequenceName = "seq_cliente", allocationSize = 1)
    private Long id_cliente;

    @NotBlank(message = "O tenant é obrigatorio!")
    @Column(name = "id_tenant", nullable = false)
    private String idTenant;

    @Enumerated(EnumType.STRING)
    private TipoCliente tp_cliente;

    @NotBlank(message = "O CPF/CNPJ é obrigatorio!")
    private String nu_cpfcnpj;

    @NotBlank(message = "O nome é obrigatorio!")
    private String nm_cliente;

    private String ds_email;
    private String nu_telefone;
    private String nu_cep;
    private String ds_logradouro;
    private String ds_complemento;
    private String ds_bairro;
    private String ds_cidade;
    private String ds_estado;
    private boolean fl_ativo = true;

    private String ds_observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "id_usuario")
    private Long id_usuario;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dt_cadastro;

    @PrePersist
    protected void onCreate() {
        this.dt_cadastro = LocalDateTime.now();
    }

    public Long getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Long id_cliente) {
        this.id_cliente = id_cliente;
    }

  

    public TipoCliente getTp_cliente() {
        return tp_cliente;
    }

    public void setTp_cliente(TipoCliente tp_cliente) {
        this.tp_cliente = tp_cliente;
    }

    public String getNu_cpfcnpj() {
        return nu_cpfcnpj;
    }

    public void setNu_cpfcnpj(String nu_cpfcnpj) {
        this.nu_cpfcnpj = nu_cpfcnpj;
    }

    public String getNm_cliente() {
        return nm_cliente;
    }

    public void setNm_cliente(String nm_cliente) {
        this.nm_cliente = nm_cliente;
    }

    public String getDs_email() {
        return ds_email;
    }

    public void setDs_email(String ds_email) {
        this.ds_email = ds_email;
    }

    public String getNu_telefone() {
        return nu_telefone;
    }

    public void setNu_telefone(String nu_telefone) {
        this.nu_telefone = nu_telefone;
    }

    public String getNu_cep() {
        return nu_cep;
    }

    public void setNu_cep(String nu_cep) {
        this.nu_cep = nu_cep;
    }

    public String getDs_logradouro() {
        return ds_logradouro;
    }

    public void setDs_logradouro(String ds_logradouro) {
        this.ds_logradouro = ds_logradouro;
    }

    public String getDs_complemento() {
        return ds_complemento;
    }

    public void setDs_complemento(String ds_complemento) {
        this.ds_complemento = ds_complemento;
    }

    public String getDs_bairro() {
        return ds_bairro;
    }

    public void setDs_bairro(String ds_bairro) {
        this.ds_bairro = ds_bairro;
    }

    public String getDs_cidade() {
        return ds_cidade;
    }

    public void setDs_cidade(String ds_cidade) {
        this.ds_cidade = ds_cidade;
    }

    public String getDs_estado() {
        return ds_estado;
    }

    public void setDs_estado(String ds_estado) {
        this.ds_estado = ds_estado;
    }

    public boolean isFl_ativo() {
        return fl_ativo;
    }

    public void setFl_ativo(boolean fl_ativo) {
        this.fl_ativo = fl_ativo;
    }

    public String getDs_observacoes() {
        return ds_observacoes;
    }

    public void setDs_observacoes(String ds_observacoes) {
        this.ds_observacoes = ds_observacoes;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Long id_usuario) {
        this.id_usuario = id_usuario;
    }

    public LocalDateTime getDt_cadastro() {
        return dt_cadastro;
    }

    public void setDt_cadastro(LocalDateTime dt_cadastro) {
        this.dt_cadastro = dt_cadastro;
    }

    public String getIdTenant() {
        return idTenant;
    }

    public void setIdTenant(String idTenant) {
        this.idTenant = idTenant;
    }

}