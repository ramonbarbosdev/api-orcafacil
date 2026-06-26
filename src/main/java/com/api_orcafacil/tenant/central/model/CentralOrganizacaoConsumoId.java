package com.api_orcafacil.tenant.central.model;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class CentralOrganizacaoConsumoId implements Serializable {

    private Long idOrganizacao;
    private String nmChaveLimite;
    private LocalDate dtReferencia;
}
