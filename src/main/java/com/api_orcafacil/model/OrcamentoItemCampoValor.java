package com.api_orcafacil.model;

import java.math.BigDecimal;

import com.api_orcafacil.common.TipoCampoValor;
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
@Table(name = "orcamento_item_campo_valor")
public class OrcamentoItemCampoValor extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento_item_campo_valor")
    @SequenceGenerator(name = "seq_orcamento_item_campo_valor", sequenceName = "seq_orcamento_item_campo_valor", allocationSize = 1)
    @Column(name = "id_orcamentoitemcampovalor")
    private Long idOrcamentoItemCampoValor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orcamentoitem", nullable = false)
    @JsonIgnore
    private OrcamentoItem orcamentoItem;

    @Column(name = "id_campopersonalizado", nullable = false)
    private Long idCampoPersonalizado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_campopersonalizado", insertable = false, updatable = false)
    @JsonIgnore
    private CampoPersonalizado campoPersonalizado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_valor", nullable = false, length = 30)
    private TipoCampoValor tpValor = TipoCampoValor.PRECO_FIXO;

    @Column(name = "vl_informado", nullable = false, precision = 18, scale = 4)
    private BigDecimal vlInformado;

    @Column(name = "ds_descricao")
    private String dsDescricao;

    public String getNmCampoPersonalizado() {
        return campoPersonalizado != null ? campoPersonalizado.getNmCampoPersonalizado() : null;
    }
}
