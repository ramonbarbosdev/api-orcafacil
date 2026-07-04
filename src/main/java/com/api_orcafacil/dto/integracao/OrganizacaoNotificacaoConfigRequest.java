package com.api_orcafacil.dto.integracao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizacaoNotificacaoConfigRequest {

    private Long idOrganizacaoNotificacao;
    /** Chave completa nak_prefixo.segredo. Vazio = nao alterar. */
    private String apiKey;
    /** E-mail do administrador para alertas de falha na notificacao-api. */
    private String emailAlertas;
}
