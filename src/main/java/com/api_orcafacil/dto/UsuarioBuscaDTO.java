package com.api_orcafacil.dto;

public record UsuarioBuscaDTO(
        boolean encontrado,
        Long idUsuario,
        String nuCpf,
        String nmUsuario,
        String tipoGlobal) {

    public static UsuarioBuscaDTO naoEncontrado() {
        return new UsuarioBuscaDTO(false, null, null, null, null);
    }
}
