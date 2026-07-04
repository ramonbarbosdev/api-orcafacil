package com.api_orcafacil.notificacao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoIntegracaoStatusDTO {

    private boolean habilitada;
    private boolean conectada;
    private Long idOrganizacaoOrcafacil;
    private Long idOrganizacaoNotificacao;
    private String mensagem;
    private boolean apiKeyValida;
    private boolean whatsappConectado;
    private String whatsappStatus;
    private String whatsappTelefone;
    private boolean usaApiKeyTenant;
}
