package com.api_orcafacil.model;

import com.api_orcafacil.common.TipoCliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cliente")
public class Cliente extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cliente")
    @SequenceGenerator(name = "seq_cliente", sequenceName = "seq_cliente", allocationSize = 1)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_cliente")
    private TipoCliente tpCliente;

    @Column(name = "nu_cpfcnpj", nullable = false)
    private String nuCpfcnpj;

    @Column(name = "nm_cliente", nullable = false)
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

    @Column(name = "ds_estado", length = 2)
    private String dsEstado;

    @Column(name = "fl_ativo", nullable = false)
    private boolean flAtivo = true;

    @Column(name = "ds_observacoes", columnDefinition = "text")
    private String dsObservacoes;

    @Column(name = "id_usuario")
    private Long idUsuario;
}
