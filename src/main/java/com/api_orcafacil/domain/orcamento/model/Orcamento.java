package com.api_orcafacil.domain.orcamento.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.api_orcafacil.domain.cliente.model.Cliente;
import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @Column(name = "cd_orcamento", nullable = false)
    @NotBlank(message = "O código é obrigatorio!")
    private String cdOrcamento;

    @Column(name = "dt_emissao", nullable = false, updatable = false)
    @NotBlank(message = "O emissão é obrigatorio!")
    private LocalDate dtEmissao;

    @Column(name = "dt_valido", nullable = false, updatable = false)
    @NotBlank(message = "O válido é obrigatorio!")
    private LocalDate dtValido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    @JsonIgnore
    private Cliente cliente;

    @Column(name = "id_cliente", nullable = false)
    @NotBlank(message = "O cliente é obrigatorio!")
    private Long idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresametodoprecificacao", insertable = false, updatable = false)
    @JsonIgnore
    private EmpresaMetodoPrecificacao empresaMetodoPrecificacao;

    @Column(name = "id_empresametodoprecificacao", nullable = false)
    private Long idEmpresaMetodoPrecificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_codicaopagamento", insertable = false, updatable = false)
    @JsonIgnore
    private CodicaoPagamento condicaopagamento;

    @Column(name = "id_codicaopagamento", nullable = false)
    @NotBlank(message = "A condição de  pagamento é obrigatorio!")
    private Long idCondicaoPagamento;

    @Column(name = "vl_custobase", nullable = false, precision = 18, scale = 4)
    @NotBlank(message = "O Valor custo base é obrigatorio!")
    private BigDecimal vlCustoBase;

    @Column(name = "vl_precobase", nullable = false, precision = 18, scale = 4)
    @NotBlank(message = "O Preço base é obrigatorio!")
    private BigDecimal vlPrecoBase;

    @Column(name = "vl_precofinal", nullable = false, precision = 18, scale = 4)
    @NotBlank(message = "O Preço Final é obrigatorio!")
    private BigDecimal vlPrecoFinal;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }
}