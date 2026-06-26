package com.api_orcafacil.model;

import com.api_orcafacil.common.StatusOrcamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orcamento_status_historico")
public class OrcamentoStatusHistorico extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento_status_historico")
    @SequenceGenerator(name = "seq_orcamento_status_historico", sequenceName = "seq_orcamento_status_historico", allocationSize = 1)
    @Column(name = "id_orcamentostatushistorico")
    private Long idOrcamentoStatusHistorico;

    @Column(name = "id_orcamento", nullable = false)
    private Long idOrcamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orcamento", insertable = false, updatable = false)
    @JsonIgnore
    private Orcamento orcamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_status_anterior", length = 30)
    private StatusOrcamento tpStatusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_status_novo", nullable = false, length = 30)
    private StatusOrcamento tpStatusNovo;
}
