package com.api_orcafacil.tenant.central.model;

import java.time.LocalDateTime;

import com.api_orcafacil.tenant.OrganizationStatus;
import com.api_orcafacil.tenant.StorageMode;

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
@Table(name = "organizacao")
public class CentralOrganizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_central_organizacao")
    @SequenceGenerator(name = "seq_central_organizacao", sequenceName = "seq_central_organizacao", allocationSize = 1)
    @Column(name = "id_organizacao")
    private Long idOrganizacao;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "nm_organizacao", nullable = false)
    private String nmOrganizacao;

    @Column(name = "ds_documento")
    private String dsDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrganizationStatus status;

    @Column(name = "database_name", nullable = false)
    private String databaseName;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_mode", nullable = false)
    private StorageMode storageMode;

    @Column(name = "id_planoassinatura")
    private Long idPlanoAssinatura;

    @Column(name = "ds_email")
    private String dsEmail;

    @Column(name = "nu_telefone")
    private String nuTelefone;

    @Column(name = "ds_webhook_url")
    private String dsWebhookUrl;

    /** ID da organizacao correspondente na notificacao-api (multi-tenant). */
    @Column(name = "id_organizacao_notificacao")
    private Long idOrganizacaoNotificacao;

    /** API Key da notificacao-api para integracao M2M (nak_xxx.chave). */
    @Column(name = "ds_api_key_notificacao", length = 512)
    private String dsApiKeyNotificacao;

    /** E-mail para receber alertas de falha na integracao de notificacoes. */
    @Column(name = "ds_email_alertas_notificacao")
    private String dsEmailAlertasNotificacao;

    /** URL externa da logo (alternativa ao upload de arquivo). */
    @Column(name = "ds_logo_url", length = 512)
    private String dsLogoUrl;

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
