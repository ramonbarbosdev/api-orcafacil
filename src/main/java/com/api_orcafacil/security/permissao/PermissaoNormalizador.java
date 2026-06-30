package com.api_orcafacil.security.permissao;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

import com.api_orcafacil.exception.BusinessException;

public final class PermissaoNormalizador {

    private static final Pattern NAO_ALFANUM = Pattern.compile("[^a-z0-9]+");
    private static final Pattern MULTI_HIFEN = Pattern.compile("-{2,}");

    private PermissaoNormalizador() {
    }

    /**
     * Normaliza slug de módulo no padrão do OrçaFácil: kebab-case minúsculo.
     * Ex.: "Fechamentos Mensais" → "fechamentos-mensais"
     */
    public static String normalizarModulo(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("Modulo/recurso obrigatorio");
        }
        String semAcento = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        String minusculo = semAcento.toLowerCase(Locale.ROOT);
        String substituido = NAO_ALFANUM.matcher(minusculo).replaceAll("-");
        String compacto = MULTI_HIFEN.matcher(substituido).replaceAll("-");
        String resultado = compacto.replaceAll("^-+|-+$", "");
        if (resultado.isBlank()) {
            throw new BusinessException("Modulo/recurso invalido apos normalizacao");
        }
        if (!resultado.matches("^[a-z][a-z0-9-]*$")) {
            throw new BusinessException("Modulo invalido. Use letras minusculas, numeros e hifen.");
        }
        return resultado;
    }

    public static String normalizarAcao(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("Acao obrigatoria");
        }
        String acao = valor.trim().toLowerCase(Locale.ROOT);
        if (!acao.matches("^[a-z][a-z0-9-]*$")) {
            throw new BusinessException("Acao invalida");
        }
        return acao;
    }

    public static String montarChave(String modulo, String acao) {
        return normalizarModulo(modulo) + "." + normalizarAcao(acao);
    }

    public static String moduloDaChave(String chave) {
        if (chave == null || chave.isBlank()) {
            return "";
        }
        int idx = chave.lastIndexOf('.');
        return idx >= 0 ? chave.substring(0, idx) : chave;
    }

    public static String acaoDaChave(String chave) {
        if (chave == null || chave.isBlank()) {
            return "";
        }
        int idx = chave.lastIndexOf('.');
        return idx >= 0 ? chave.substring(idx + 1) : chave;
    }
}
