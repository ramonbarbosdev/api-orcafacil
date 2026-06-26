package com.api_orcafacil.tenant.central.model;

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
@Table(name = "organizacao_logo")
public class CentralOrganizacaoLogo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_central_organizacao_logo")
    @SequenceGenerator(name = "seq_central_organizacao_logo", sequenceName = "seq_central_organizacao_logo", allocationSize = 1)
    @Column(name = "id_organizacao_logo")
    private Long idOrganizacaoLogo;

    @Column(name = "id_organizacao", nullable = false, unique = true)
    private Long idOrganizacao;

    @Column(name = "ds_caminho_interno", nullable = false, length = 500)
    private String dsCaminhoInterno;

    @Column(name = "nm_arquivo_original", length = 255)
    private String nmArquivoOriginal;

    @Column(name = "nm_arquivo_salvo", nullable = false, length = 255)
    private String nmArquivoSalvo;

    @Column(name = "ds_content_type", nullable = false, length = 100)
    private String dsContentType;

    @Column(name = "ds_extensao", nullable = false, length = 10)
    private String dsExtensao;

    @Column(name = "nu_tamanho_bytes", nullable = false)
    private Long nuTamanhoBytes;

    @Column(name = "nu_largura", nullable = false)
    private Integer nuLargura;

    @Column(name = "nu_altura", nullable = false)
    private Integer nuAltura;

    @Column(name = "id_usuario_upload")
    private Long idUsuarioUpload;

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
