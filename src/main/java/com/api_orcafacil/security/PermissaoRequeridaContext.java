package com.api_orcafacil.security;

public final class PermissaoRequeridaContext {

    public static final String REQUEST_ATTR = "orcafacil.permissaoRequerida";

    private final String modulo;
    private final String acao;

    public PermissaoRequeridaContext(String modulo, String acao) {
        this.modulo = modulo;
        this.acao = acao;
    }

    public String getModulo() {
        return modulo;
    }

    public String getAcao() {
        return acao;
    }

    public String chave() {
        return modulo + "." + acao;
    }
}
