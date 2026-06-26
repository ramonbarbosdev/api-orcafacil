package com.api_orcafacil.common;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugUtil {

    private SlugUtil() {
    }

    public static String fromNome(String value) {
        String normalized = Normalizer.normalize(value == null ? "organizacao" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? "organizacao" : normalized;
    }

    public static String databaseName(String slug) {
        return "org_" + slug.replace('-', '_');
    }
}
