package com.api_orcafacil.tenant.central.model;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class CentralPlanoLimiteId implements Serializable {

    private Long idPlanoAssinatura;
    private String nmChaveLimite;
}
