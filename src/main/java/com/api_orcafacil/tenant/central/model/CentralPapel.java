package com.api_orcafacil.tenant.central.model;

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
@Table(name = "papel")
public class CentralPapel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_central_papel")
    @SequenceGenerator(name = "seq_central_papel", sequenceName = "seq_central_papel", allocationSize = 1)
    @Column(name = "id_papel")
    private Long idPapel;

    @Column(name = "nm_papel", nullable = false)
    private String nmPapel;

    @Column(name = "fl_ativo", nullable = false)
    private boolean flAtivo = true;
}
