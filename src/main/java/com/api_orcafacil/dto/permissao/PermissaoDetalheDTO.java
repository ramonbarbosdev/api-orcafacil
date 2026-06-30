package com.api_orcafacil.dto.permissao;

public record PermissaoDetalheDTO(
        Long idPermissao,
        String nmChave,
        String modulo,
        String acao,
        String descricao,
        boolean flAtivo) {
}
