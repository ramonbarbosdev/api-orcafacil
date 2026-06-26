package com.api_orcafacil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "categoria_servico")
public class CategoriaServico extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_categoriaservico")
    @SequenceGenerator(name = "seq_categoriaservico", sequenceName = "seq_categoriaservico", allocationSize = 1)
    @Column(name = "id_categoriaservico")
    private Long idCategoriaServico;

    @Column(name = "cd_categoriaservico", nullable = false, length = 50)
    private String cdCategoriaServico;

    @Column(name = "nm_categoriaservico", nullable = false)
    private String nmCategoriaServico;

    @Column(name = "ds_categoriaservico", columnDefinition = "text")
    private String dsCategoriaServico;
}
