package com.api_orcafacil.notificacao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WhatsappSessaoStatusDTO {

    private Boolean sucesso;
    private Long idOrganizacao;
    private String status;
    private Boolean conectado;
    private String qr;
    private String qrImagem;
    private String telefone;
    private String erro;
}
