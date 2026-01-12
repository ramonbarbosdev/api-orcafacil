package com.api_orcafacil.domain.orcamento.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.api_orcafacil.domain.cliente.model.Cliente;
import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orcamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento")
    @SequenceGenerator(name = "seq_orcamento", sequenceName = "seq_orcamento", allocationSize = 1)
    @Column(name = "id_orcamento")
    private Long idOrcamento;

    @Column(name = "id_tenant", nullable = false)
    @NotBlank(message = "O tenant é obrigatorio!")
    private String idTenant;

    @Column(name = "nu_orcamento", nullable = false)
    @NotBlank(message = "O número é obrigatorio!")
    private String nuOrcamento;

    @Column(name = "dt_emissao", nullable = false)
    @NotNull(message = "O emissão é obrigatorio!")
    private LocalDate dtEmissao;

    @Column(name = "dt_valido", nullable = false)
    @NotNull(message = "O válido é obrigatorio!")
    private LocalDate dtValido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    // @JsonIgnore
    private Cliente cliente;

    @Column(name = "id_cliente", nullable = false)
    @NotNull(message = "O cliente é obrigatorio!")
    private Long idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresametodoprecificacao", insertable = false, updatable = false)
    @JsonIgnore
    private EmpresaMetodoPrecificacao empresaMetodoPrecificacao;

    @Column(name = "id_empresametodoprecificacao")
    private Long idEmpresaMetodoPrecificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_codicaopagamento", insertable = false, updatable = false)
    @JsonIgnore
    private CodicaoPagamento condicaopagamento;

    @Column(name = "id_codicaopagamento", nullable = false)
    @NotNull(message = "A condição de  pagamento é obrigatorio!")
    private Long idCondicaoPagamento;

    @Column(name = "nu_prazoentrega", nullable = false)
    @NotNull(message = "O Prazo de entrega é obrigatorio!")
    private Integer nuPrazoEntrega;

    @Column(name = "ds_observacoes", columnDefinition = "TEXT")
    private String dsObservacoes;

    @Column(name = "vl_precobase", nullable = false, precision = 18, scale = 4)
    @NotNull(message = "O Preço base é obrigatorio!")
    private BigDecimal vlPrecoBase;

    @Column(name = "vl_precofinal", precision = 18, scale = 4)
    private BigDecimal vlPrecoFinal;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrcamentoItem> orcamentoItem = new ArrayList<>();

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }

    @JsonProperty("nmCliente")
    public String getNmCliente() {

        if (cliente != null) {
            return cliente.getNmCliente();
        }
        return null;
    }

    @JsonProperty("dsMetodoPrecificacao")
    public String getDsMetodoPrecificacao() {

        if (empresaMetodoPrecificacao != null) {
            return empresaMetodoPrecificacao.getDsMetodoPrecificacao();
        }
        return null;
    }
}