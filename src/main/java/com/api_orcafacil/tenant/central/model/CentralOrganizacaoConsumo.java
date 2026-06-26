package com.api_orcafacil.tenant.central.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "organizacao_consumo")
@IdClass(CentralOrganizacaoConsumoId.class)
public class CentralOrganizacaoConsumo {

    @Id
    @Column(name = "id_organizacao")
    private Long idOrganizacao;

    @Id
    @Column(name = "nm_chave_limite", length = 80)
    private String nmChaveLimite;

    @Id
    @Column(name = "dt_referencia")
    private LocalDate dtReferencia;

    @Column(name = "nu_consumo", nullable = false)
    private Long nuConsumo = 0L;
}
