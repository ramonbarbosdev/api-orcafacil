package com.api_orcafacil.notificacao.dto;

public record NotificacaoSelecionarOrganizacaoResponse(
        String token,
        Long idOrganizacao,
        String role) {
}
