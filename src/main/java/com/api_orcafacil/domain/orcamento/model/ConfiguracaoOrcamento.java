package com.api_orcafacil.domain.orcamento.model;

import java.time.LocalDateTime;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "configuracao_orcamento")
@Getter
@Setter
public class ConfiguracaoOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracaoorcamento")
    private Long idConfiguracaoOrcamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empresa", insertable = false, updatable = false)
    @JsonIgnore
    private Empresa empresa;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "id_tenant", nullable = false)
    private String idTenant;

    @Column(name = "prefixo_numero", nullable = false, length = 10)
    private String prefixoNumero = "ORC";

    @Column(name = "validade_dias", nullable = false)
    private Integer validadeDias = 30;

        @Column(name = "termos_padrao", length = 100)
    private String termosPadrao;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @Column(name = "dt_atualizacao", nullable = false)
    private LocalDateTime dtAtualizacao;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
        this.dtAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dtAtualizacao = LocalDateTime.now();
    }
}