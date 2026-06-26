package com.api_orcafacil.tenant.central.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "plano_assinatura")
public class CentralPlanoAssinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_planoassinatura")
    @SequenceGenerator(name = "seq_planoassinatura", sequenceName = "seq_planoassinatura", allocationSize = 1)
    @Column(name = "id_planoassinatura")
    private Long idPlanoAssinatura;

    @Column(name = "nm_planoassinatura", nullable = false)
    private String nmPlanoAssinatura;

    @Column(name = "vl_mensal")
    private BigDecimal vlMensal;

    @Column(name = "nu_limitemensagens", nullable = false)
    private int nuLimiteMensagens;

    @Column(name = "nu_limiteatendentes", nullable = false)
    private int nuLimiteAtendentes;

    @Column(name = "fl_ativo", nullable = false)
    private boolean flAtivo = true;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @Column(name = "dt_atualizacao", nullable = false)
    private LocalDateTime dtAtualizacao;

    @PrePersist
    protected void onCreate() {
        LocalDateTime agora = LocalDateTime.now();
        this.dtCriacao = agora;
        this.dtAtualizacao = agora;
    }

    @PreUpdate
    protected void onUpdate() {
        this.dtAtualizacao = LocalDateTime.now();
    }
}
