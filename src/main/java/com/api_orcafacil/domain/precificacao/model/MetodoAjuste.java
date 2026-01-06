package com.api_orcafacil.domain.precificacao.model;

import java.time.LocalDateTime;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.enums.TipoAjuste;
import com.api_orcafacil.enums.TipoOperacaoAjuste;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "metodo_ajustes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetodoAjuste {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_metodo_ajuste")
    @SequenceGenerator(name = "seq_metodo_ajuste", sequenceName = "seq_metodo_ajuste", allocationSize = 1)
    @Column(name = "id_metodoajuste")
    private Long idMetodoAjuste;

    @Column(name = "id_tenant", nullable = false)
    @NotNull(message = "O tenant é obrigatorio!")
    private String idTenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresametodoprecificacao", insertable = false, updatable = false)
    @JsonIgnore
    private EmpresaMetodoPrecificacao empresaMetodoPrecificacao;

    @Column(name = "id_empresametodoprecificacao", nullable = false)
    @NotNull(message = "O identificador da emmpresa metodo é obrigatorio!")
    private Long idEmpresaMetodoPrecificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_campopersonalizado", insertable = false, updatable = false)
    @JsonIgnore
    private CampoPersonalizado campoPersonalizado;

    @Column(name = "id_campopersonalizado")
    @NotNull(message = "O campo personalizado é obrigatorio!")
    private Long idCampoPersonalizado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_ajuste", nullable = false)
    @NotNull(message = "O tipo de ajuste é obrigatorio!")
    private TipoAjuste tpAjuste;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_operacao", nullable = false)
    @NotNull(message = "O tipo da operação é obrigatorio!")
    private TipoOperacaoAjuste tpOperacao;

    @Column(name = "vl_condicao", nullable = false)
    @NotBlank(message = "O valor esperado é obrigatorio!")
    private String vlCondicao;

    @Column(name = "vl_incremento", nullable = false)
    @NotNull(message = "O valor de ajuste é obrigatorio!")
    private Double vlIncremento;
    // Ex: 0.2, 0.3

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }
}