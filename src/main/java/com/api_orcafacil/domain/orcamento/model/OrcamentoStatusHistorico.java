package com.api_orcafacil.domain.orcamento.model;

import java.time.LocalDateTime;

import com.api_orcafacil.domain.usuario.model.Usuario;
import com.api_orcafacil.enums.StatusOrcamento;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orcamento_status_historico")
public class OrcamentoStatusHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento_status_historico")
    @SequenceGenerator(name = "seq_orcamento_status_historico", sequenceName = "seq_orcamento_status_historico", allocationSize = 1)
    @Column(name = "id_statushistorico")
    private Long idStatusHistorico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orcamento", nullable = false)
    @JsonIgnore
    private Orcamento orcamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_status", nullable = false)
    private StatusOrcamento tpStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }

}
