package com.api_orcafacil.notificacao.support;

public record NotificacaoErroUsuario(
        String codigoErro,
        String mensagemUsuario,
        String mensagemTecnica,
        boolean notificarEquipe) {
}
