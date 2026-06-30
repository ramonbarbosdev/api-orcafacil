package com.api_orcafacil.security.permissao;

import java.util.Optional;

public enum PermissaoAcaoHttp {

    EXIBIR("exibir"),
    LER("ler"),
    CRIAR("criar"),
    EDITAR("editar"),
    DELETAR("deletar");

    private final String codigo;

    PermissaoAcaoHttp(String codigo) {
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }

    public static Optional<String> acaoPorMetodoHttp(String method) {
        if (method == null) {
            return Optional.empty();
        }
        return switch (method.toUpperCase()) {
            case "GET", "HEAD", "OPTIONS" -> Optional.of(LER.codigo);
            case "POST" -> Optional.of(CRIAR.codigo);
            case "PUT", "PATCH" -> Optional.of(EDITAR.codigo);
            case "DELETE" -> Optional.of(DELETAR.codigo);
            default -> Optional.empty();
        };
    }

    public static boolean isAcaoPadrao(String acao) {
        if (acao == null || acao.isBlank()) {
            return false;
        }
        for (PermissaoAcaoHttp item : values()) {
            if (item.codigo.equals(acao)) {
                return true;
            }
        }
        return false;
    }
}
