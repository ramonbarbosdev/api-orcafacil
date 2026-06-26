package com.api_orcafacil.tenant.central.model;

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
@Table(name = "plano_limite")
@IdClass(CentralPlanoLimiteId.class)
public class CentralPlanoLimite {

    @Id
    @Column(name = "id_planoassinatura")
    private Long idPlanoAssinatura;

    @Id
    @Column(name = "nm_chave_limite", length = 80)
    private String nmChaveLimite;

    @Column(name = "nu_valor")
    private Long nuValor;
}
