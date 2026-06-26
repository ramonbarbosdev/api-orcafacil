package com.api_orcafacil.tenant.central.model;

import java.time.LocalDateTime;

import com.api_orcafacil.common.TipoGlobal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "usuario_global")
public class CentralUsuarioGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_central_usuario")
    @SequenceGenerator(name = "seq_central_usuario", sequenceName = "seq_central_usuario", allocationSize = 1)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nu_cpf", nullable = false)
    private String nuCpf;

    @Column(name = "nm_usuario")
    private String nmUsuario;

    @Column(name = "nm_email")
    private String nmEmail;

    @Column(name = "ds_senha", nullable = false)
    private String dsSenha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_global", nullable = false)
    private TipoGlobal tpGlobal = TipoGlobal.DEFAULT;

    @Column(name = "ds_foto_url")
    private String dsFotoUrl;

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
