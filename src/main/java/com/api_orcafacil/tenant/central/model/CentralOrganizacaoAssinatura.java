package com.api_orcafacil.tenant.central.model;

import java.time.LocalDateTime;

import com.api_orcafacil.common.AssinaturaStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "organizacao_assinatura")
public class CentralOrganizacaoAssinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_organizacaoassinatura")
    private Long idOrganizacaoAssinatura;

    @Column(name = "id_organizacao", nullable = false)
    private Long idOrganizacao;

    @Column(name = "id_planoassinatura", nullable = false)
    private Long idPlanoAssinatura;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_status", nullable = false, length = 30)
    private AssinaturaStatus tpStatus = AssinaturaStatus.ATIVA;

    @Column(name = "dt_inicio", nullable = false)
    private LocalDateTime dtInicio;

    @Column(name = "dt_fim")
    private LocalDateTime dtFim;

    @Column(name = "dt_fim_trial")
    private LocalDateTime dtFimTrial;

    @Column(name = "dt_proximo_ciclo")
    private LocalDateTime dtProximoCiclo;

    @Column(name = "fl_renovacao_automatica", nullable = false)
    private boolean flRenovacaoAutomatica = true;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @Column(name = "dt_atualizacao", nullable = false)
    private LocalDateTime dtAtualizacao;

    @PrePersist
    void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        if (dtInicio == null) {
            dtInicio = agora;
        }
        dtCriacao = agora;
        dtAtualizacao = agora;
    }

    @PreUpdate
    void preUpdate() {
        dtAtualizacao = LocalDateTime.now();
    }
}
