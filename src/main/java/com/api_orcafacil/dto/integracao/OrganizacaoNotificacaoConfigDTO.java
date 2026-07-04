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
    /** Indica se ha API Key configurada (valor mascarado). */
    private String apiKeyMascarada;
    private boolean usaApiKeyTenant;
    private boolean usaApiKeyGlobal;
}
