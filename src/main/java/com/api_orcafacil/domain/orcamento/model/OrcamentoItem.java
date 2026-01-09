package com.api_orcafacil.domain.orcamento.model;

import java.math.BigDecimal;

import com.api_orcafacil.domain.catalogo.model.Catalogo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orcamento", insertable = false, updatable = false)
    @JsonIgnore
    private Orcamento orcamento;

    @Column(name = "id_orcamento", nullable = false)
    private Long idOrcamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_catalogo", insertable = false, updatable = false)
    @JsonIgnore
    private Catalogo catalogo;

    @Column(name = "id_catalogo", nullable = false)
    @NotNull(message = "A catalogo é obrigatorio!")
    private Long idCatalogo;

    @Column(name = "qt_item", nullable = false, precision = 18, scale = 4)
    @NotNull(message = "A quantidade é obrigatorio!")
    private BigDecimal qtItem;

    @Column(name = "vl_custounitario", nullable = false, precision = 18, scale = 4)
    @NotNull(message = "O valor custo unitario é obrigatorio!")
    private BigDecimal vlCustoUnitario;

    @Column(name = "vl_precounitario", nullable = false, precision = 18, scale = 4)
    @NotNull(message = "O preco unitario é obrigatorio!")
    private BigDecimal vlPrecoUnitario;

    @Column(name = "vl_precototal", nullable = false, precision = 18, scale = 4)
    @NotNull(message = "O preco total unitario é obrigatorio!")
    private BigDecimal vlPrecoTotal;
}