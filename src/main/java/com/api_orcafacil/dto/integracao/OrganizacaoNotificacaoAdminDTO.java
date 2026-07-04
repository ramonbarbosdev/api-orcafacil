package com.api_orcafacil.dto.integracao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacaoNotificacaoAdminDTO {

    private Long idOrganizacaoOrcafacil;
    private Long idOrganizacaoNotificacao;
    private String apiKey;
    private boolean configurada;
    private boolean habilitada;
    private String emailAlertas;
}
