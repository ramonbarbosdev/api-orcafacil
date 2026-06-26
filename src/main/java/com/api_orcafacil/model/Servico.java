package com.api_orcafacil.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "servico")
public class Servico extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_servico")
    @SequenceGenerator(name = "seq_servico", sequenceName = "seq_servico", allocationSize = 1)
    @Column(name = "id_servico")
    private Long idServico;

    @Column(name = "id_categoriaservico")
    private Long idCategoriaServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoriaservico", insertable = false, updatable = false)
    @JsonIgnore
    private CategoriaServico categoriaServico;

    @Column(name = "cd_servico", nullable = false, length = 50)
    private String cdServico;

    @Column(name = "nm_servico", nullable = false)
    private String nmServico;

    @Column(name = "ds_servico", columnDefinition = "text")
    private String dsServico;

    @Column(name = "vl_custo", precision = 18, scale = 4)
    private BigDecimal vlCusto;

    @Column(name = "vl_preco", precision = 18, scale = 4)
    private BigDecimal vlPreco;
}
