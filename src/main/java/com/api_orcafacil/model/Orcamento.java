package com.api_orcafacil.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.api_orcafacil.common.StatusOrcamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orcamento")
public class Orcamento extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento")
    @SequenceGenerator(name = "seq_orcamento", sequenceName = "seq_orcamento", allocationSize = 1)
    @Column(name = "id_orcamento")
    private Long idOrcamento;

    @Column(name = "nu_orcamento", nullable = false, length = 50)
    private String nuOrcamento;

    @Column(name = "dt_emissao", nullable = false)
    private LocalDate dtEmissao;

    @Column(name = "dt_valido", nullable = false)
    private LocalDate dtValido;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    private Cliente cliente;

    @Column(name = "id_empresametodoprecificacao")
    private Long idEmpresaMetodoPrecificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresametodoprecificacao", insertable = false, updatable = false)
    @JsonIgnore
    private EmpresaMetodoPrecificacao empresaMetodoPrecificacao;

    @Column(name = "id_codicaopagamento", nullable = false)
    private Long idCondicaoPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_codicaopagamento", insertable = false, updatable = false)
    @JsonIgnore
    private CondicaoPagamento condicaoPagamento;

    @Column(name = "nu_prazoentrega", nullable = false)
    private Integer nuPrazoEntrega = 20;

    @Column(name = "ds_observacoes", columnDefinition = "text")
    private String dsObservacoes;

    @Column(name = "vl_precobase", nullable = false, precision = 18, scale = 4)
    private BigDecimal vlPrecoBase;

    @Column(name = "vl_precofinal", precision = 18, scale = 4)
    private BigDecimal vlPrecoFinal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_status", nullable = false, length = 30)
    private StatusOrcamento tpStatus = StatusOrcamento.RASCUNHO;

    @Column(name = "cd_publico", unique = true, length = 64)
    private String cdPublico;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrcamentoItem> itens = new ArrayList<>();

    @JsonProperty("nmCliente")
    public String getNmCliente() {
        return cliente != null ? cliente.getNmCliente() : null;
    }

    @JsonProperty("nmCondicaoPagamento")
    public String getNmCondicaoPagamento() {
        return condicaoPagamento != null ? condicaoPagamento.getNmCondicaoPagamento() : null;
    }
}
