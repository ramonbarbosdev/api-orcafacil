package com.api_orcafacil.dto.integracao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacaoNotificacaoTenantDTO {

    private boolean habilitada;
    private boolean configurada;
    private String mensagem;
}
