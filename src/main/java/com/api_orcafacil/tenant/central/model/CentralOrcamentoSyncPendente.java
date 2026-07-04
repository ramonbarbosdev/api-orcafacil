package com.api_orcafacil.tenant.central.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orcamento_sync_pendente")
public class CentralOrcamentoSyncPendente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento_sync_pendente")
    @SequenceGenerator(name = "seq_orcamento_sync_pendente", sequenceName = "seq_orcamento_sync_pendente", allocationSize = 1)
    @Column(name = "id_orcamento_sync_pendente")
    private Long idOrcamentoSyncPendente;

    @Column(name = "id_organizacao", nullable = false)
    private Long idOrganizacao;

    @Column(name = "id_orcamento")
    private Long idOrcamento;

    @Column(name = "cd_publico", length = 64)
    private String cdPublico;

    @Column(name = "tp_operacao", nullable = false, length = 20)
    private String tpOperacao;

    @Column(name = "nu_tentativas", nullable = false)
    private int nuTentativas;

    @Column(name = "fl_novo", nullable = false)
    private boolean flNovo = true;

    @Column(name = "ds_erro", columnDefinition = "text")
    private String dsErro;

    @Column(name = "dt_proximo_retry", nullable = false)
    private LocalDateTime dtProximoRetry;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @PrePersist
    void prePersist() {
        if (dtCriacao == null) {
            dtCriacao = LocalDateTime.now();
        }
    }
}
