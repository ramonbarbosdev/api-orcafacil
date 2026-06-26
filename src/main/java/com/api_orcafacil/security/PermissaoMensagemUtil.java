package com.api_orcafacil.security;

import java.util.Map;

public final class PermissaoMensagemUtil {

    private static final Map<String, String> ACAO = Map.of(
            "ler", "visualizar",
            "criar", "cadastrar",
            "editar", "editar",
            "deletar", "excluir");

    private static final Map<String, String> MODULO = Map.ofEntries(
            Map.entry("clientes", "clientes"),
            Map.entry("catalogos", "catálogos"),
            Map.entry("servicos", "serviços"),
            Map.entry("categorias-servico", "categorias de serviço"),
            Map.entry("orcamentos", "orçamentos"),
            Map.entry("condicoes-pagamento", "condições de pagamento"),
            Map.entry("configuracao-orcamento", "configurações de orçamento"),
            Map.entry("metodos-precificacao", "métodos de precificação"),
            Map.entry("campos-personalizados", "campos personalizados"),
            Map.entry("metodos-ajuste", "métodos de ajuste"),
            Map.entry("empresa-metodos-precificacao", "métodos de precificação da empresa"),
            Map.entry("perfil", "perfil"),
            Map.entry("organizacao", "logo da organização"));

    private PermissaoMensagemUtil() {
    }

    public static String mensagemAcessoNegado(String chavePermissao) {
        if (chavePermissao == null || chavePermissao.isBlank()) {
            return "Você não tem permissão para realizar esta operação.";
        }
        String[] partes = chavePermissao.split("\\.", 2);
        if (partes.length != 2) {
            return "Você não tem permissão para realizar esta operação.";
        }
        String acao = ACAO.getOrDefault(partes[1], partes[1]);
        String modulo = labelModulo(partes[0]);
        return "Você não tem permissão para " + acao + " " + modulo + ".";
    }

    private static String labelModulo(String slug) {
        return MODULO.getOrDefault(slug, humanizar(slug));
    }

    private static String humanizar(String slug) {
        return slug.replace('-', ' ');
    }
}
