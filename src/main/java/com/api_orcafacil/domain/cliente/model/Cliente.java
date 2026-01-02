package com.api_orcafacil.domain.cliente.model;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cliente")
    @SequenceGenerator(name = "seq_cliente", sequenceName = "seq_cliente", allocationSize = 1)
    @Column(name = "id_cliente")
    private Long idCliente;

    @NotBlank(message = "O tenant é obrigatorio!")
    @Column(name = "id_tenant", nullable = false)
    private String idTenant;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_cliente")
    private TipoCliente tpCliente;

    @NotBlank(message = "O CPF/CNPJ é obrigatorio!")
    @Column(name = "nu_cpfcnpj")
    private String nuCpfcnpj;

    @NotBlank(message = "O nome é obrigatorio!")
    @Column(name = "nm_cliente")
    private String nmCliente;

    @Column(name = "ds_email")
    private String dsEmail;
    @Column(name = "nu_telefone")
    private String nuTelefone;
    @Column(name = "nu_cep")
    private String nuCep;
    @Column(name = "ds_logradouro")
    private String dsLogradouro;
    @Column(name = "ds_complemento")
    private String dsComplemento;
    @Column(name = "ds_bairro")
    private String dsBairro;
    @Column(name = "ds_cidade")
    private String dsCidade;
    @Column(name = "ds_estado")
    private String dsEstado;
    @Column(name = "fl_ativo")
    private boolean flAtivo = true;

    @Column(name = "ds_observacoes")
    private String dsObservacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }

}