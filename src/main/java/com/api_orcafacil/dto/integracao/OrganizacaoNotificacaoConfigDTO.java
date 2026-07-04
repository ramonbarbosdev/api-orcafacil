package com.api_orcafacil.dto.integracao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacaoNotificacaoConfigDTO {

    private Long idOrganizacaoOrcafacil;
    private Long idOrganizacaoNotificacao;
    /** API Key completa da organizacao (visivel para o admin do tenant). */
    private String apiKey;
    private boolean configurada;
    /** E-mail para alertas operacionais da integracao. */
    private String emailAlertas;
}
