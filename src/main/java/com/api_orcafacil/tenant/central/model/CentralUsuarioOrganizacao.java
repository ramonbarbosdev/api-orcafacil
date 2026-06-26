package com.api_orcafacil.tenant.central.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuario_organizacao")
public class CentralUsuarioOrganizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_central_usuario_organizacao")
    @SequenceGenerator(name = "seq_central_usuario_organizacao", sequenceName = "seq_central_usuario_organizacao", allocationSize = 1)
    @Column(name = "id_usuario_organizacao")
    private Long idUsuarioOrganizacao;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_organizacao", nullable = false)
    private Long idOrganizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private CentralUsuarioGlobal usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizacao", insertable = false, updatable = false)
    private CentralOrganizacao organizacao;

    @Column(name = "ds_role", nullable = false)
    private String dsRole;

    @Column(name = "fl_ativo", nullable = false)
    private boolean flAtivo = true;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @PrePersist
    protected void onCreate() {
        this.dtCriacao = LocalDateTime.now();
    }
}
