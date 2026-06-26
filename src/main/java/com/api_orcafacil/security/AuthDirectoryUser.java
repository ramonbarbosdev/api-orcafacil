package com.api_orcafacil.security;

public record AuthDirectoryUser(
        Long idUsuario,
        String nuCpf,
        String nmUsuario,
        String nmEmail,
        String dsSenha,
        String tipoGlobal,
        boolean flAtivo) {
}
