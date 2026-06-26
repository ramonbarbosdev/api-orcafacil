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
@Table(name = "configuracao_orcamento")
public class ConfiguracaoOrcamento extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_configuracaoorcamento")
    @SequenceGenerator(name = "seq_configuracaoorcamento", sequenceName = "seq_configuracaoorcamento", allocationSize = 1)
    @Column(name = "id_configuracaoorcamento")
    private Long idConfiguracaoOrcamento;

    @Column(name = "prefixo_numero", nullable = false, length = 10)
    private String prefixoNumero;

    @Column(name = "validade_dias", nullable = false)
    private Integer validadeDias;

    @Column(name = "termos_padrao", columnDefinition = "text")
    private String termosPadrao;
}
