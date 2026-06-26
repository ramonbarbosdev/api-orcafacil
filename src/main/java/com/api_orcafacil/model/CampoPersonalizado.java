package com.api_orcafacil.model;

import com.api_orcafacil.common.TipoCampoValor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "campos_personalizados")
public class CampoPersonalizado extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_campo_personalizado")
    @SequenceGenerator(name = "seq_campo_personalizado", sequenceName = "seq_campo_personalizado", allocationSize = 1)
    @Column(name = "id_campopersonalizado")
    private Long idCampoPersonalizado;

    @Column(name = "cd_campopersonalizado", nullable = false, length = 50)
    private String cdCampoPersonalizado;

    @Column(name = "nm_campopersonalizado", nullable = false, length = 100)
    private String nmCampoPersonalizado;

    @Column(name = "ds_campopersonalizado")
    private String dsCampoPersonalizado;

    @Column(name = "tp_campopersonalizado", nullable = false, length = 20)
    private String tpCampoPersonalizado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_campovalor", nullable = false, length = 30)
    private TipoCampoValor tpCampoValor = TipoCampoValor.PRECO_FIXO;
}
