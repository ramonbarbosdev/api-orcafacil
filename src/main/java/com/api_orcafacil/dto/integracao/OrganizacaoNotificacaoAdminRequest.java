package com.api_orcafacil.dto.integracao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizacaoNotificacaoAdminRequest {

    private Long idOrganizacaoNotificacao;
    /** Chave completa nak_prefixo.segredo. Vazio = nao alterar. */
    private String apiKey;
    private String emailAlertas;
    private Boolean habilitada;
}
