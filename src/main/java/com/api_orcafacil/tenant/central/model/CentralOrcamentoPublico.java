package com.api_orcafacil.tenant.central.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orcamento_publico")
public class CentralOrcamentoPublico {

    @Id
    @Column(name = "cd_publico", length = 64)
    private String cdPublico;

    @Column(name = "id_organizacao", nullable = false)
    private Long idOrganizacao;

    @Column(name = "id_orcamento", nullable = false)
    private Long idOrcamento;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @PrePersist
    void prePersist() {
        if (dtCriacao == null) {
            dtCriacao = LocalDateTime.now();
        }
    }
}
