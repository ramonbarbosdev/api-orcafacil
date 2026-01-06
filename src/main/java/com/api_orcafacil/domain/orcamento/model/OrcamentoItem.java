package com.api_orcafacil.domain.orcamento.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "orcamento_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento_item")
    @SequenceGenerator(name = "seq_orcamento_item", sequenceName = "seq_orcamento_item", allocationSize = 1)
    @Column(name = "id_orcamentoitem")
    private Long idOrcamentoItem;

    @Column(name = "id_tenant", nullable = false)
    @NotBlank(message = "O tenant é obrigatorio!")
    private String idTenant;

    /* ===== Orçamento ===== */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orcamento", insertable = false, updatable = false)
    @JsonIgnore
    private Orcamento orcamento;

    @Column(name = "id_orcamento", nullable = false)
    private Long idOrcamento;

    /* ===== Item ===== */

    @Column(name = "ds_item", nullable = false, length = 255)
    @NotBlank(message = "A descrição é obrigatorio!")
    private String dsItem;

    @Column(name = "qt_item", nullable = false, precision = 18, scale = 4)
    @NotBlank(message = "A quantidade é obrigatorio!")
    private BigDecimal qtItem;

    @Column(name = "vl_custounitario", nullable = false, precision = 18, scale = 4)
    @NotBlank(message = "O valor custo unitario é obrigatorio!")
    private BigDecimal vlCustoUnitario;

    /* ===== Resultado do cálculo ===== */

    @Column(name = "vl_precounitario", nullable = false, precision = 18, scale = 4)
    @NotBlank(message = "O preco unitario é obrigatorio!")
    private BigDecimal vlPrecoUnitario;

    @Column(name = "vl_precototal", nullable = false, precision = 18, scale = 4)
    @NotBlank(message = "O preco total unitario é obrigatorio!")
    private BigDecimal vlPrecoTotal;
}