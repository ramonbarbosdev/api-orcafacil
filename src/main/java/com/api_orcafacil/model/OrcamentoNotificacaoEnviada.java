package com.api_orcafacil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orcamento_notificacao")
public class OrcamentoNotificacaoEnviada extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento_notificacao")
    @SequenceGenerator(name = "seq_orcamento_notificacao", sequenceName = "seq_orcamento_notificacao", allocationSize = 1)
    @Column(name = "id_orcamento_notificacao")
    private Long idOrcamentoNotificacao;

    @Column(name = "id_orcamento", nullable = false)
    private Long idOrcamento;

    @Column(name = "tp_canal", nullable = false, length = 30)
    private String tpCanal;

    @Column(name = "ds_destinatario", length = 255)
    private String dsDestinatario;

    @Column(name = "fl_sucesso", nullable = false)
    private boolean flSucesso;

    @Column(name = "id_notificacao_externa")
    private Long idNotificacaoExterna;

    @Column(name = "ds_erro", columnDefinition = "text")
    private String dsErro;

    @Column(name = "ds_mensagem", columnDefinition = "text")
    private String dsMensagem;
}
